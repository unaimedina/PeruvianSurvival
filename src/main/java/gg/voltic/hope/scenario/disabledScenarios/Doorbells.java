package gg.voltic.hope.scenario.disabledScenarios;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.Cooldown;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.LocationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ShapelessRecipe;

public class Doorbells extends Scenario {
   private final List<Material> doorbells = new ArrayList();
   private final HashMap<Location, Cooldown> cooldowns = new HashMap<>();

   public Doorbells() {
      super("Doorbells", "Integrates a doorbell system.", new FileConfig(Hope.getInstance(), "doorbells.yml"));
      this.doorbells.add(Material.STONE_BUTTON);
      this.doorbells.add(Material.OAK_BUTTON);
      this.doorbells.add(Material.BIRCH_BUTTON);
      this.doorbells.add(Material.ACACIA_BUTTON);
      this.doorbells.forEach(doorbell -> {
         NamespacedKey key = new NamespacedKey(Hope.getInstance(), "doorbell_" + doorbell.name());
         ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemCreator(doorbell).setName("&aTimbre").get());
         recipe.addIngredient(doorbell);
         recipe.addIngredient(Material.IRON_INGOT);
         Bukkit.addRecipe(recipe);
      });
   }

   @EventHandler
   public void onDoorbellPlace(BlockPlaceEvent event) {
      if (!event.isCancelled()) {
         if (event.getItemInHand() != null && event.getItemInHand().getType() != Material.AIR) {
            if (this.doorbells.contains(event.getItemInHand().getType())) {
               ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
               List<String> locations = cursor.getStringList("locations");
               locations.add(LocationUtil.serialize(event.getBlockPlaced().getLocation()));
               cursor.set("locations", locations);
               cursor.save();
            }
         }
      }
   }

   @EventHandler
   public void onDoorbellBreak(BlockDestroyEvent event) {
      if (!event.isCancelled()) {
         ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
         List<String> locations = cursor.getStringList("locations");
         if (locations.contains(LocationUtil.serialize(event.getBlock().getLocation()))) {
            locations.remove(LocationUtil.serialize(event.getBlock().getLocation()));
            cursor.set("locations", locations);
            cursor.save();
         }
      }
   }

   @EventHandler
   public void onDoorbellInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Block clicked = event.getClickedBlock();
         ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");
         List<String> locations = cursor.getStringList("locations");
         if (locations.contains(LocationUtil.serialize(clicked.getLocation()))) {
            if (this.cooldowns.containsKey(clicked.getLocation())) {
               Cooldown cooldown = this.cooldowns.get(clicked.getLocation());
               if (cooldown.hasExpired()) {
                  event.getPlayer().playSound(clicked.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 100.0F);
                  event.getPlayer().playSound(clicked.getLocation(), Sound.BLOCK_BELL_USE, 0.2F, 100.0F);
                  this.getNearbyPlayers(event.getPlayer()).forEach(player -> {
                     player.playSound(clicked.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 100.0F);
                     player.playSound(clicked.getLocation(), Sound.BLOCK_BELL_USE, 0.2F, 100.0F);
                  });
                  this.cooldowns.put(clicked.getLocation(), new Cooldown(1300L));
               }
            } else {
               event.getPlayer().playSound(clicked.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 100.0F);
               event.getPlayer().playSound(clicked.getLocation(), Sound.BLOCK_BELL_USE, 0.2F, 100.0F);
               this.getNearbyPlayers(event.getPlayer()).forEach(player -> {
                  player.playSound(clicked.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 100.0F);
                  player.playSound(clicked.getLocation(), Sound.BLOCK_BELL_USE, 0.2F, 100.0F);
               });
               this.cooldowns.put(clicked.getLocation(), new Cooldown(1300L));
            }
         }
      }
   }

   private List<Player> getNearbyPlayers(Player p) {
      List<Player> nearby = new ArrayList();
      p.getNearbyEntities(10.0, 5.0, 10.0).stream().filter(entity -> entity instanceof Player).forEach(entity -> nearby.add((Player)entity));
      return nearby;
   }
}
