package gg.voltic.hope.scenario.disabledScenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.ItemCreator;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Backpacks extends Scenario {
   private final ItemStack backpack = new ItemCreator(Material.CHEST_MINECART)
      .setName("&aMochila")
      .setLore(Collections.singletonList("&7Click derecho para abrirla"))
      .get();

   public Backpacks() {
      super("Backpacks", "Integrates backpacks connected to players' enderchests.", null);
      NamespacedKey backpackKey = new NamespacedKey(Hope.getInstance(), "backpack");
      ShapedRecipe backpackRecipe = new ShapedRecipe(backpackKey, this.backpack);
      backpackRecipe.shape("L L", "LCL", " D ");
      backpackRecipe.setIngredient('L', Material.LEATHER);
      backpackRecipe.setIngredient('C', Material.CHEST);
      backpackRecipe.setIngredient('D', Material.DIAMOND);
      Bukkit.addRecipe(backpackRecipe);
   }

   @EventHandler
   public void onBackpackInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
         ItemStack is = event.getItem();
         if (is == null || is.getType() == Material.AIR) {
            return;
         }

         if (!is.hasItemMeta() || !is.getItemMeta().hasDisplayName() || !is.getItemMeta().hasLore()) {
            return;
         }

         if (is.equals(this.backpack)) {
            event.getPlayer().openInventory(event.getPlayer().getEnderChest());
            event.setCancelled(true);
         }
      }
   }
}
