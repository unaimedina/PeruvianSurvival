package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.InventorySave;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.LocationUtil;
import gg.voltic.hope.utils.TaskUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Graves extends Scenario {
   private final ConfigCursor cursor;
   private final ItemCreator keyCreator = new ItemCreator(Material.TRIPWIRE_HOOK)
      .setName("&aLlave de tumba")
      .addEnchants(List.of("DURABILITY,10"))
      .hideEnchants();
   private final List<Location> graves = new ArrayList<>();
   private final Map<String, List<Location>> pendingKeys = new HashMap<>();
   private final Map<String, InventorySave> pendingInventories = new HashMap<>();

   public Graves() {
      super("Graves", "When you die, you drop your items and your inventory is saved to a chest.", new FileConfig(Hope.getInstance(), "graves.yml"));
      this.cursor = new ConfigCursor(this.getConfig(), "");
      this.cursor.getStringList("graves").forEach(string -> {
         if (string.contains(";")) {
            String[] split = string.split(";");
            String locString = split[0];
            Location location = LocationUtil.deserialize(locString);

             this.graves.add(location);
         } else {
            Location location = LocationUtil.deserialize(string);
             this.graves.add(location);
         }
      });
      Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, !this.isEnabled()));
      (new BukkitRunnable() {
            public void run() {
               Graves.this.cursor
                  .getStringList("graves")
                  .stream()
                  .filter(s -> s.contains(";"))
                  .forEach(
                     string -> {
                        String[] split = string.split(";");
                        String locString = split[0];
                        String timestamp = split[1];
                        Location location = LocationUtil.deserialize(locString);
                         if (Common.isLong(timestamp)) {
                             if (System.currentTimeMillis() - Long.parseLong(timestamp) >= 1800000L) {
                               Graves.this.graves.remove(location);
                               Graves.this.breakDoubleChest(location.getBlock());
                               Graves.this.cursor
                                       .set(
                                               "graves",
                                               Graves.this.cursor.getStringList("graves").stream().filter(s -> !s.equals(string)).collect(Collectors.toList())
                                       );
                               Graves.this.cursor.save();
                             }
                         }
                     }
                  );
               Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aTodas las tumbas de más de 30 minutos han sido abiertas!"));
            }
         })
         .runTaskTimer(Hope.getInstance(), 36000L, 36000L);
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      Player player = event.getPlayer();
      if (!this.createGrave(player, player.getLocation(), event.getDrops())) {
         event.setDroppedExp(0);
      }

      event.getDrops().clear();
   }

   @EventHandler
   public void keyOnRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      if (this.pendingKeys.containsKey(player.getName())) {
         this.giveKey(player, this.pendingKeys.get(player.getName()));
         this.pendingKeys.remove(player.getName());
      }
   }

   @EventHandler
   public void inventoryOnRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      if (this.pendingInventories.containsKey(player.getName())) {
         TaskUtil.runLater(() -> {
            InventorySave inventorySave = this.pendingInventories.get(player.getName());
            Common.changePlayerExp(player, inventorySave.getExp());
            Map<Integer, ItemStack> pending = player.getInventory().addItem(inventorySave.getInventory().toArray(new ItemStack[0]));
            if (!pending.isEmpty()) {
               pending.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            }

            this.pendingInventories.remove(player.getName());
         }, 20L);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onGraveInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
               if (this.graves.contains(event.getClickedBlock().getLocation())) {
                  if (event.getItem() == null
                     || !event.getItem().hasItemMeta()
                     || !event.getItem().getItemMeta().hasDisplayName()
                     || !event.getItem().getItemMeta().hasLore()
                     || event.getItem().getType() != Material.TRIPWIRE_HOOK) {
                     event.setCancelled(true);
                  } else if (!event.getItem().getItemMeta().getDisplayName().equals(this.keyCreator.get().getItemMeta().getDisplayName())) {
                     event.setCancelled(true);
                  } else {
                     List<String> lore = Common.clearList(event.getItem().getItemMeta().getLore());
                     lore.forEach(s -> {
                        Location location = LocationUtil.deserialize(s);
                        if (location != null) {
                           if (event.getClickedBlock().getLocation().equals(location)) {
                              this.breakDoubleChest(event.getClickedBlock());
                              event.getPlayer().getInventory().remove(event.getItem());
                              event.getPlayer().updateInventory();
                              event.setCancelled(true);
                           }
                        }
                     });
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onGraveExplode(BlockExplodeEvent event) {
      event.blockList().removeIf(blk -> this.graves.contains(blk.getLocation()));
      if (this.graves.contains(event.getBlock().getLocation())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onGraveExplode(EntityExplodeEvent event) {
      event.blockList().removeIf(blk -> this.graves.contains(blk.getLocation()));
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onGraveBreak(BlockBreakEvent event) {
      if (event.getBlock().getType() == Material.CHEST) {
         if (this.graves.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onHopperExtractGrave(InventoryMoveItemEvent event) {
      if (event.getSource().getType() == InventoryType.CHEST) {
         Block block = event.getSource().getLocation().getBlock();
         if (this.graves.contains(block.getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPistonExpandProtectedBlock(BlockPistonExtendEvent event) {
      event.getBlocks().forEach(block -> {
         if (this.graves.contains(block.getLocation())) {
            event.setCancelled(true);
         }
      });
   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public void onKeyInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
         if (event.getItem() != null) {
            if (event.getItem().getType() == Material.TRIPWIRE_HOOK) {
               if (event.getItem().hasItemMeta()) {
                  if (event.getItem().getItemMeta().hasDisplayName()) {
                     if (event.getItem().getItemMeta().hasLore()) {
                        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) {
                           if (event.getItem().getItemMeta().getDisplayName().equals(this.keyCreator.get().getItemMeta().getDisplayName())) {
                              event.setCancelled(true);
                              List<String> lore = Common.clearList(event.getItem().getItemMeta().getLore());
                              Location location = LocationUtil.deserialize(lore.get(0));
                              if (location == null) {
                                 location = LocationUtil.deserialize(lore.get(1));
                                 if (location == null) {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo se puede encontrar la tumba."));
                                 } else if (!this.graves.contains(location)) {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo se puede encontrar la tumba."));
                                 } else {
                                    event.getPlayer().teleport(location.clone().add(0.0, 1.0, 0.0));
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHas sido teletransportado a tu tumba."));
                                 }
                              } else if (!this.graves.contains(location)) {
                                 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo se puede encontrar la tumba."));
                              } else {
                                 event.getPlayer().teleport(location.clone().add(0.0, 1.0, 0.0));
                                 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHas sido teletransportado a tu tumba."));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean createGrave(Player player, Location location, List<ItemStack> drops) {
      if (location.getWorld().getEnvironment() == Environment.THE_END && location.getY() < 0.0) {
         location.setY(Math.abs(location.getY()));
      }

      int exp = player.getPlayer().getExpToLevel();
      String safeFaces = this.searchSafeFaces(location);
      if (safeFaces == null) {
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cNo se pudo crear la tumba."));
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &aAparecerás con tu inventario!"));
         this.pendingInventories
            .put(
               player.getName(),
               new InventorySave(player, drops.stream().filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR).toList())
            );
         return false;
      } else {
         String[] split = safeFaces.split(";");
         Block leftBlock = location.getBlock();
         Block rightBlock = location.getBlock().getRelative(BlockFace.valueOf(split[1]));
         leftBlock.setType(Material.CHEST);
         rightBlock.setType(Material.CHEST);
         Chest leftChest = (Chest)leftBlock.getBlockData();
         leftChest.setType(Type.LEFT);
         leftChest.setFacing(BlockFace.valueOf(split[0]));
         leftBlock.setBlockData(leftChest);
         Chest rightChest = (Chest)rightBlock.getBlockData();
         rightChest.setType(Type.RIGHT);
         rightChest.setFacing(BlockFace.valueOf(split[0]));
         rightBlock.setBlockData(rightChest);
         if (rightBlock.getRelative(BlockFace.valueOf(split[0])).getType() == Material.AIR) {
            Location signLocation = rightBlock.getRelative(BlockFace.valueOf(split[0])).getLocation();
            signLocation.getBlock().setType(Material.OAK_WALL_SIGN);
            BlockData signBlockData = signLocation.getBlock().getBlockData();
            ((Directional)signBlockData).setFacing(BlockFace.valueOf(split[0]));
            signLocation.getBlock().setBlockData(signBlockData);
            Sign sign = (Sign)signLocation.getBlock().getState();
            sign.setLine(0, "================");
            sign.setLine(1, "Tumba de");
            sign.setLine(2, player.getName());
            sign.setLine(3, "================");
            sign.setGlowingText(true);
            sign.update();
         }

         org.bukkit.block.Chest chest = (org.bukkit.block.Chest)location.getBlock().getState();
         drops.stream()
            .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR)
            .forEach(itemStack -> chest.getInventory().addItem(new ItemStack[]{itemStack}));
         location.getWorld().spawn(location, ExperienceOrb.class).setExperience(exp);
         this.pendingKeys.put(player.getName(), Arrays.asList(rightBlock.getLocation(), leftBlock.getLocation()));
         this.graves.add(leftBlock.getLocation());
         this.graves.add(rightBlock.getLocation());
         this.cursor
            .set("graves", this.graves.stream().map(loc -> LocationUtil.serialize(loc) + ";" + System.currentTimeMillis()).toArray(x$0 -> new String[x$0]));
         this.cursor.save();
         return true;
      }
   }

   private void breakDoubleChest(Block block) {
      BlockState doubleChest = block.getState();
      if (doubleChest instanceof org.bukkit.block.Chest chest && ((org.bukkit.block.Chest)block.getState()).getInventory() instanceof DoubleChestInventory) {
         DoubleChest doubleChestx = (DoubleChest)chest.getInventory().getHolder();
         org.bukkit.block.Chest right = (org.bukkit.block.Chest)doubleChestx.getRightSide();
         Block rightBlock = right.getBlock();
         org.bukkit.block.Chest left = (org.bukkit.block.Chest)doubleChestx.getLeftSide();
         Block leftBlock = left.getBlock();

         for(ItemStack item : doubleChestx.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
               block.getWorld().dropItemNaturally(block.getLocation(), item);
            }
         }

         rightBlock.setType(Material.AIR);
         rightBlock.getState().update(true);
         leftBlock.setType(Material.AIR);
         leftBlock.getState().update(true);
         this.graves.remove(leftBlock.getLocation());
         this.graves.remove(rightBlock.getLocation());
         this.cursor
            .set("graves", this.graves.stream().map(loc -> LocationUtil.serialize(loc) + ";" + System.currentTimeMillis()).toArray(x$0 -> new String[x$0]));
         this.cursor.save();
      }
   }

   private void giveKey(Player player, List<Location> locations) {
      this.keyCreator.setLore(Arrays.asList(locations.stream().map(loc -> "&c&k" + LocationUtil.serialize(loc)).toArray(x$0 -> new String[x$0])));
      player.getInventory().addItem(this.keyCreator.get());
   }

   private String searchSafeFaces(Location location) {
      BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
      if (location.getBlock().getType() != Material.AIR) {
         return null;
      } else {
         for(int i = 0; i < faces.length; ++i) {
            BlockFace nextFace;
            if (i == faces.length - 1) {
               nextFace = faces[0];
            } else {
               nextFace = faces[i + 1];
            }

            Block block = location.getBlock().getRelative(nextFace);
            if (block.getType() == Material.AIR) {
               return faces[i] + ";" + nextFace;
            }
         }

         return null;
      }
   }
}
