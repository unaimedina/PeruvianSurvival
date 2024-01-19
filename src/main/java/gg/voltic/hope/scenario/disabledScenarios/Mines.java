package gg.voltic.hope.scenario.disabledScenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.LocationUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Mines extends Scenario {
   private final List<Mine> mineList = new ArrayList<>();
   private final List<Location> mines = new ArrayList();
   private final List<Material> minesMaterials = new ArrayList();
   private final ItemStack deactivator = new ItemCreator(Material.DIAMOND_HOE)
      .setName("&aDesactivador de bombas")
      .setLore(Arrays.asList("&7Click derecho apuntando hacia una bomba para desactivarla", "", "&aUsos restantes: &c10/10"))
      .get();
   private final ItemStack detonator = new ItemCreator(Material.CLOCK)
      .setName("&aDetonador a distancia")
      .setLore(Arrays.asList("&7Click derecho hacia una mina para vincularte", "", "&aVinculado a: &cNADA"))
      .get();

   public Mines() {
      super("Mines", "Integrates a mine system with remote detonators and more", new FileConfig(Hope.getInstance(), "mines.yml"));
      this.minesMaterials.add(Material.DIRT);
      this.minesMaterials.add(Material.COBBLESTONE);
      this.minesMaterials.add(Material.STONE);
      this.minesMaterials
         .forEach(
            material -> {
               Mine mine = new Mine(
                  new ItemCreator(material).setName("&cMina").setLore(Arrays.asList("&7Click derecho para colocarla", "&7Al romperla explotará")).get()
               );
               NamespacedKey mineKey = new NamespacedKey(Hope.getInstance(), material.name().toLowerCase() + "_mine");
               ShapelessRecipe mineRecipe = new ShapelessRecipe(mineKey, mine.getItemStack());
               mineRecipe.addIngredient(Material.TNT);
               mineRecipe.addIngredient(material);
               mine.setKey(mineKey);
               mine.setRecipe(mineRecipe);
               this.mineList.add(mine);
            }
         );
      this.mineList.forEach(mine -> Bukkit.addRecipe(mine.getRecipe()));
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
      cursor.getStringList("locations").forEach(location -> this.mines.add(LocationUtil.deserialize(location)));
      NamespacedKey deactivatorKey = new NamespacedKey(Hope.getInstance(), "mine_deactivator");
      ShapelessRecipe deactivatorRecipe = new ShapelessRecipe(deactivatorKey, this.deactivator);
      deactivatorRecipe.addIngredient(Material.DIAMOND_HOE);
      deactivatorRecipe.addIngredient(Material.TNT);
      Bukkit.addRecipe(deactivatorRecipe);
      NamespacedKey detonatorKey = new NamespacedKey(Hope.getInstance(), "mine_detonator");
      ShapedRecipe detonatorRecipe = new ShapedRecipe(detonatorKey, this.detonator);
      detonatorRecipe.shape("ICI", " T ", " R ");
      detonatorRecipe.setIngredient('I', Material.IRON_INGOT);
      detonatorRecipe.setIngredient('C', Material.CLOCK);
      detonatorRecipe.setIngredient('T', Material.TNT);
      detonatorRecipe.setIngredient('R', Material.REDSTONE);
      Bukkit.addRecipe(detonatorRecipe);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onMinePlace(BlockPlaceEvent event) {
      if (!event.isCancelled()) {
         ItemStack item = event.getItemInHand();
         if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
            if (this.mineList
               .stream()
               .anyMatch(
                  mine -> item.getType() == mine.getItemStack().getType()
                        && item.getItemMeta().getDisplayName().equals(mine.getItemStack().getItemMeta().getDisplayName())
                        && item.getItemMeta().getLore().equals(mine.getItemStack().getItemMeta().getLore())
               )) {
               this.addLocation(event.getBlock().getLocation());
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onMineBreak(BlockBreakEvent event) {
      if (!event.isCancelled()) {
         Location location = event.getBlock().getLocation();
         if (this.mines.contains(location)) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
               location.getWorld().createExplosion(location, 5.0F);
            }

            this.removeLocation(location);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onMineExplode(BlockExplodeEvent event) {
      if (!event.isCancelled()) {
         Location location = event.getBlock().getLocation();
         event.blockList().forEach(block -> {
            if (this.mines.contains(block.getLocation())) {
               this.removeLocation(block.getLocation());
            }
         });
         if (this.mines.contains(location)) {
            this.removeLocation(location);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onMineExplode(EntityExplodeEvent event) {
      if (!event.isCancelled()) {
         event.blockList().forEach(block -> {
            if (this.mines.contains(block.getLocation())) {
               this.removeLocation(block.getLocation());
            }
         });
      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onMinePhysics(BlockPhysicsEvent event) {
      if (!event.isCancelled()) {
         Location location = event.getSourceBlock().getLocation();
         if (this.mines.contains(location)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onDeactivatorInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         ItemStack itemStack = event.getItem();
         if (itemStack == null
            || itemStack.getType() == Material.AIR
            || !itemStack.hasItemMeta()
            || !itemStack.getItemMeta().hasDisplayName()
            || !itemStack.getItemMeta().hasLore()) {
            return;
         }

         final Location location = event.getClickedBlock().getLocation();
         ItemStack deactivator = this.deactivator;
         if (itemStack.getType() == deactivator.getType() && itemStack.getItemMeta().getDisplayName().equals(deactivator.getItemMeta().getDisplayName())) {
            if (this.mines.contains(location)) {
               Player player = event.getPlayer();
               int uses = Integer.parseInt(ChatColor.stripColor(itemStack.getLore().get(2)).replaceAll("Usos restantes: ", "").split("/")[0]);
               if (uses - 1 == 0) {
                  player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10.0F, 0.2F);
                  player.getInventory().removeItem(itemStack);
               } else {
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  List<String> newLore = itemMeta.getLore();
                  newLore.set(2, ChatColor.translateAlternateColorCodes('&', "&aUsos restantes: &c" + --uses + "/10"));
                  itemMeta.setLore(newLore);
                  itemStack.setItemMeta(itemMeta);
               }

               if (event.getPlayer().getGameMode() != GameMode.CREATIVE && Common.getRandom(100) >= 80) {
                  player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLa bomba no se ha podido desactivar, explotará en 5 segundos..."));
                  player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10.0F, 0.2F);
                  (new BukkitRunnable() {
                     public void run() {
                        location.getWorld().createExplosion(location, 5.0F);
                        Mines.this.removeLocation(location);
                     }
                  }).runTaskLater(Hope.getInstance(), 100L);
               } else {
                  player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aBomba desactivada correctamente."));
                  player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 10.0F, 0.2F);
                  event.getClickedBlock().breakNaturally();
                  this.removeLocation(location);
               }
            }

            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onDetonatorInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         ItemStack itemStack = event.getItem();
         if (itemStack == null
            || itemStack.getType() == Material.AIR
            || !itemStack.hasItemMeta()
            || !itemStack.getItemMeta().hasDisplayName()
            || !itemStack.getItemMeta().hasLore()) {
            return;
         }

         Location location = event.getClickedBlock().getLocation();
         ItemStack detonator = this.detonator;
         if (itemStack.getType() == detonator.getType()
            && itemStack.getItemMeta().getDisplayName().equals(detonator.getItemMeta().getDisplayName())
            && ChatColor.stripColor(itemStack.getLore().get(2)).contains("NADA")
            && this.mines.contains(location)) {
            Player player = event.getPlayer();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemStack.getLore();
            lore.set(2, ChatColor.translateAlternateColorCodes('&', "&aVinculado a: &c" + LocationUtil.serialize(location)));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 0.2F);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aMina vinculada correctamente"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aClick derecho con el detonador en la mano y explotará."));
            event.setCancelled(true);
            return;
         }
      }

      if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
         ItemStack itemStack = event.getItem();
         if (itemStack == null
            || itemStack.getType() == Material.AIR
            || !itemStack.hasItemMeta()
            || !itemStack.getItemMeta().hasDisplayName()
            || !itemStack.getItemMeta().hasLore()) {
            return;
         }

         ItemStack detonator = this.detonator;
         if (itemStack.getType() == detonator.getType() && itemStack.getItemMeta().getDisplayName().equals(detonator.getItemMeta().getDisplayName())) {
            Player player = event.getPlayer();
            if (!ChatColor.stripColor(itemStack.getLore().get(2)).contains("NADA")) {
               Location possibleLocation = LocationUtil.deserialize(ChatColor.stripColor(itemStack.getLore().get(2)).replaceAll("Vinculado a: ", ""));
               if (this.mines.contains(possibleLocation)) {
                  possibleLocation.getWorld().createExplosion(possibleLocation, 5.0F);
                  this.removeLocation(possibleLocation);
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  List<String> lore = itemStack.getLore();
                  lore.set(2, ChatColor.translateAlternateColorCodes('&', "&aVinculado a: &cNADA"));
                  itemMeta.setLore(lore);
                  itemStack.setItemMeta(itemMeta);
                  player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aMina explotada correctamente."));
               } else {
                  player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo se ha encontrado la mina vinculada, puede ser que ya haya explotado."));
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  List<String> lore = itemStack.getLore();
                  lore.set(2, ChatColor.translateAlternateColorCodes('&', "&aVinculado a: &cNADA"));
                  itemMeta.setLore(lore);
                  itemStack.setItemMeta(itemMeta);
               }
            } else {
               player.sendMessage(
                  ChatColor.translateAlternateColorCodes('&', "&cNo tienes ninguna mina vinculada, click derecho apuntando hacia una para vincularla.")
               );
            }

            event.setCancelled(true);
         }
      }
   }

   private void saveLocations() {
      List<String> locations = new ArrayList<>();
      this.mines.forEach(location -> locations.add(LocationUtil.serialize(location)));
      ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
      cursor.set("locations", locations);
      cursor.save();
   }

   private void addLocation(Location location) {
      this.mines.add(location);
      this.saveLocations();
   }

   private void removeLocation(Location location) {
      this.mines.remove(location);
      this.saveLocations();
   }

   static class Mine {
      private ItemStack itemStack;
      private NamespacedKey key;
      private Recipe recipe;

      public Mine(ItemStack itemStack, NamespacedKey key, Recipe recipe) {
         this.itemStack = itemStack;
         this.key = key;
         this.recipe = recipe;
      }

      public Mine(ItemStack itemStack) {
         this.itemStack = itemStack;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public NamespacedKey getKey() {
         return this.key;
      }

      public Recipe getRecipe() {
         return this.recipe;
      }

      public void setItemStack(ItemStack itemStack) {
         this.itemStack = itemStack;
      }

      public void setKey(NamespacedKey key) {
         this.key = key;
      }

      public void setRecipe(Recipe recipe) {
         this.recipe = recipe;
      }
   }
}
