package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Protections extends Scenario {
   private final List<Protection> protections = new ArrayList<>();
   private static final HashMap<Block, Protection> protectionsByBlock = new HashMap<>();
   private final HashMap<Long, Protection> protectionsById = new HashMap<>();
   private static final HashMap<Block, Protection> protectionsBySign = new HashMap<>();

   public Protections() {
      super("Protections", "Integrates a protection system with a sign to the server.", new FileConfig(Hope.getInstance(), "protected.yml"), Material.TNT);
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");

      for(String key : cursor.getKeys("blocks")) {
         this.protections
            .add(
               new Protection(
                  Common.fromString(cursor.getString("blocks." + key + ".sign")),
                  Common.fromString(cursor.getString("blocks." + key + ".blockProtected")),
                  Bukkit.getOfflinePlayer(cursor.getUuid("blocks." + key + ".owner")),
                  Long.parseLong(key)
               )
            );
      }

      this.protections.forEach(protection -> protectionsByBlock.put(protection.getBlockProtected(), protection));
      this.protections.forEach(protection -> this.protectionsById.put(protection.getId(), protection));
      this.protections.forEach(protection -> protectionsBySign.put(protection.getSign(), protection));
   }

   @EventHandler
   public void onProtect(SignChangeEvent event) {
      String[] lines = event.getLines();
      Block signBlock = event.getBlock();
      if (signBlock.getState().getBlockData() instanceof WallSign signData) {
         BlockFace attached = signData.getFacing().getOppositeFace();
         Block blockAttached = signBlock.getRelative(attached);
         if (lines[0].equalsIgnoreCase("[Protect]")) {
            long id = 1L + (long)(Math.random() * 99999.0);
            event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&c[Protected]"));
            event.setLine(1, ChatColor.translateAlternateColorCodes('&', "&l" + event.getPlayer().getName()));
            event.setLine(2, ChatColor.translateAlternateColorCodes('&', "&7&k" + id));
            this.save(new Protection(signBlock, blockAttached, event.getPlayer(), id));
            this.reload();
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (Common.isSign(event.getBlock())) {
         Sign sign = (Sign)event.getBlock().getState();
         if (!sign.getLines()[0].equals("") && !sign.getLines()[1].equals("") && !sign.getLines()[2].equals("")) {
            if (Common.isLong(ChatColor.stripColor(sign.getLines()[2]))) {
               if (this.protectionsById.containsKey(Long.valueOf(ChatColor.stripColor(sign.getLines()[2])))) {
                  Protection protection = this.protectionsById.get(Long.valueOf(ChatColor.stripColor(sign.getLines()[2])));
                  if (protection.getOwner() == null) {
                     if (protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                        this.remove(protection);
                        this.reload();
                     } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
                     }
                  } else if (protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                     this.remove(protection);
                     this.reload();
                  } else {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
                  }
               }
            }
         }
      } else {
         if (protectionsByBlock.containsKey(block)) {
            Protection protection = protectionsByBlock.get(block);
            if (protection.getOwner() == null) {
               if (protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  this.remove(protection);
                  this.reload();
               } else {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
               }
            } else if (protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
               this.remove(protection);
               this.reload();
            } else {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockPlace(final BlockPlaceEvent event) {
      final Block theChestBlock = event.getBlock();
      BlockState chestState = theChestBlock.getState();
      if (chestState instanceof Chest chest) {
         (new BukkitRunnable() {
            public void run() {
               Inventory inventory = chest.getInventory();
               if (inventory instanceof DoubleChestInventory) {
                  DoubleChest doubleChest = (DoubleChest)inventory.getHolder();
                  Chest right = (Chest)doubleChest.getRightSide();
                  Block rightBlock = right.getBlock();
                  if (Protections.protectionsByBlock.containsKey(rightBlock)) {
                     Protection protection = Protections.protectionsByBlock.get(rightBlock);
                     if (protection.getOwner() == null) {
                        if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                           theChestBlock.breakNaturally();
                           event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                           return;
                        }
                     } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                        theChestBlock.breakNaturally();
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                        return;
                     }
                  }

                  Chest left = (Chest)doubleChest.getLeftSide();
                  Block leftBlock = left.getBlock();
                  if (Protections.protectionsByBlock.containsKey(leftBlock)) {
                     Protection protection = Protections.protectionsByBlock.get(leftBlock);
                     if (protection.getOwner() == null) {
                        if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                           theChestBlock.breakNaturally();
                           event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                        }
                     } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                        theChestBlock.breakNaturally();
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                     }
                  }
               }
            }
         }).runTaskLater(Hope.getInstance(), 1L);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Block block = event.getClickedBlock();
         if (block == null) {
            return;
         }

         if (block.getState() instanceof Chest chest && chest.getInventory() instanceof DoubleChestInventory) {
            DoubleChest doubleChest = (DoubleChest)chest.getInventory().getHolder();
            Chest right = (Chest)doubleChest.getRightSide();
            Block rightBlock = right.getBlock();
            if (protectionsByBlock.containsKey(rightBlock)) {
               Protection protection = protectionsByBlock.get(rightBlock);
               if (protection.getOwner() == null) {
                  if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                     return;
                  }
               } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                  return;
               }
            }

            Chest left = (Chest)doubleChest.getLeftSide();
            Block leftBlock = left.getBlock();
            if (protectionsByBlock.containsKey(leftBlock)) {
               Protection protection = protectionsByBlock.get(leftBlock);
               if (protection.getOwner() == null) {
                  if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                     return;
                  }
               } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
                  return;
               }
            }
         }

         if (protectionsByBlock.containsKey(block)) {
            Protection protection = protectionsByBlock.get(block);
            if (protection.getOwner() == null) {
               if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
               }
            } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, tonto."));
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockBurned(BlockBurnEvent event) {
      Block block = event.getBlock();
      if (isProtected(block)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockDamaged(BlockDamageEvent event) {
      Block block = event.getBlock();
      if (Common.isSign(event.getBlock())) {
         Sign sign = (Sign)event.getBlock().getState();
         if (sign.getLines().length < 3 || sign.getLines()[3].equals("")) {
            return;
         }

         if (!Common.isLong(sign.getLines()[2])) {
            return;
         }

         if (this.protectionsById.containsKey(Long.valueOf(ChatColor.stripColor(sign.getLines()[2])))) {
            Protection protection = this.protectionsById.get(Long.valueOf(ChatColor.stripColor(sign.getLines()[2])));
            if (protection.getOwner() == null) {
               if (protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  this.remove(protection);
                  this.reload();
               } else {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
               }
            } else if (protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
               this.remove(protection);
               this.reload();
            } else {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEse bloque está protegido, no lo puedes romper."));
            }
         }
      } else {
         if (checkDoubleChest(block, event.getPlayer())) {
            event.setCancelled(true);
            return;
         }

         if (protectionsByBlock.containsKey(block)) {
            Protection protection = protectionsByBlock.get(block);
            if (protection.getOwner() == null) {
               if (!protection.getOfflineOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
                  event.setCancelled(true);
               }
            } else if (!protection.getOwner().getUniqueId().toString().equals(event.getPlayer().getUniqueId().toString())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onEntityBreaksProtectedBlock(EntityExplodeEvent event) {
      List<Block> remove = new ArrayList();
      event.blockList().stream().filter(Protections::isProtected).forEach(remove::add);
      event.blockList().removeAll(remove);
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onProtectedBlockExploded(BlockExplodeEvent event) {
      event.blockList().removeIf(Protections::isProtected);
      if (isProtected(event.getBlock())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onHopperExtractProtectedBlock(InventoryMoveItemEvent event) {
      if (event.getSource().getType() == InventoryType.CHEST) {
         Block block = event.getSource().getLocation().getBlock();
         if (isProtected(block)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPistonExpandProtectedBlock(BlockPistonExtendEvent event) {
      event.getBlocks().forEach(block -> {
         if (isProtected(block)) {
            event.setCancelled(true);
         }
      });
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPistonRetractProtectedBlock(BlockPistonRetractEvent event) {
      event.getBlocks().forEach(block -> {
         if (isProtected(block)) {
            event.setCancelled(true);
         }
      });
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onRedstoneChangeProtectedBlocK(BlockRedstoneEvent event) {
      Block block = event.getBlock();
      if (isProtected(block)) {
         event.setNewCurrent(event.getOldCurrent());
      }
   }

   private static boolean checkDoubleChest(Block block) {
      BlockState doubleChest = block.getState();
      if (doubleChest instanceof Chest chest && ((Chest)block.getState()).getInventory() instanceof DoubleChestInventory) {
         DoubleChest doubleChestx = (DoubleChest)chest.getInventory().getHolder();
         Chest right = (Chest)doubleChestx.getRightSide();
         Block rightBlock = right.getBlock();
         Chest left = (Chest)doubleChestx.getLeftSide();
         Block leftBlock = left.getBlock();
         return protectionsByBlock.containsKey(rightBlock) || protectionsByBlock.containsKey(leftBlock);
      }

      return false;
   }

   private static boolean checkDoubleChest(Block block, Player player) {
      BlockState doubleChest = block.getState();
      if (doubleChest instanceof Chest chest && ((Chest)block.getState()).getInventory() instanceof DoubleChestInventory) {
         DoubleChest doubleChestx = (DoubleChest)chest.getInventory().getHolder();
         Chest right = (Chest)doubleChestx.getRightSide();
         Block rightBlock = right.getBlock();
         Chest left = (Chest)doubleChestx.getLeftSide();
         Block leftBlock = left.getBlock();
         if (protectionsByBlock.containsKey(rightBlock)) {
            Protection protection = protectionsByBlock.get(rightBlock);
            if (protection.getOwner() == null) {
               if (!protection.getOfflineOwner().getUniqueId().toString().equals(player.getUniqueId().toString())) {
                  return true;
               }
            } else if (!protection.getOwner().getUniqueId().toString().equals(player.getUniqueId().toString())) {
               return true;
            }
         }

         if (protectionsByBlock.containsKey(leftBlock)) {
            Protection protection = protectionsByBlock.get(leftBlock);
            if (protection.getOwner() == null) {
               if (!protection.getOfflineOwner().getUniqueId().toString().equals(player.getUniqueId().toString())) {
                  return true;
               }
            } else if (!protection.getOwner().getUniqueId().toString().equals(player.getUniqueId().toString())) {
               return true;
            }
         }

         return protectionsByBlock.containsKey(rightBlock) || protectionsByBlock.containsKey(leftBlock);
      }

      return false;
   }

   public void reload() {
      this.protections.clear();
      protectionsByBlock.clear();
      this.protectionsById.clear();
      protectionsBySign.clear();
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");

      for(String key : cursor.getKeys("blocks")) {
         this.protections
            .add(
               new Protection(
                  Common.fromString(cursor.getString("blocks." + key + ".sign")),
                  Common.fromString(cursor.getString("blocks." + key + ".blockProtected")),
                  Bukkit.getOfflinePlayer(cursor.getUuid("blocks." + key + ".owner")),
                  Long.parseLong(key)
               )
            );
      }

      this.protections.forEach(protection -> protectionsByBlock.put(protection.getBlockProtected(), protection));
      this.protections.forEach(protection -> this.protectionsById.put(protection.getId(), protection));
      this.protections.forEach(protection -> protectionsBySign.put(protection.getSign(), protection));
   }

   public void save(Protection protection) {
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
      cursor.set("blocks." + protection.getId() + ".sign", Common.toString(protection.getSign()));
      cursor.set("blocks." + protection.getId() + ".blockProtected", Common.toString(protection.getBlockProtected()));
      cursor.set("blocks." + protection.getId() + ".owner", protection.getOwner().getUniqueId().toString());
      cursor.save();
   }

   public void remove(Protection protection) {
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
      cursor.set("blocks." + protection.getId(), null);
      cursor.save();
   }

   public static boolean isProtected(Block block) {
      return checkDoubleChest(block) || protectionsByBlock.containsKey(block) || protectionsBySign.containsKey(block);
   }

   public List<Protection> getProtections() {
      return this.protections;
   }

   public HashMap<Long, Protection> getProtectionsById() {
      return this.protectionsById;
   }

   static class Protection {
      private final Block blockProtected;
      private Player owner;
      private OfflinePlayer offlineOwner;
      private final Block sign;
      private final long id;

      public Protection(Block sign, Block blockProtected, Player owner, long id) {
         this.blockProtected = blockProtected;
         this.owner = owner;
         this.sign = sign;
         this.id = id;
      }

      public Protection(Block sign, Block blockProtected, OfflinePlayer owner, long id) {
         this.blockProtected = blockProtected;
         this.offlineOwner = owner;
         this.sign = sign;
         this.id = id;
      }

      public Block getBlockProtected() {
         return this.blockProtected;
      }

      public Player getOwner() {
         return this.owner;
      }

      public OfflinePlayer getOfflineOwner() {
         return this.offlineOwner;
      }

      public Block getSign() {
         return this.sign;
      }

      public long getId() {
         return this.id;
      }
   }
}
