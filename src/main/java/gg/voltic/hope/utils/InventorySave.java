package gg.voltic.hope.utils;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventorySave {
   private final Player player;
   private final List<ItemStack> inventory;
   private final int exp;

   public InventorySave(Player player, List<ItemStack> inventory) {
      this.player = player;
      this.inventory = inventory;
      this.exp = Common.getPlayerExp(player);
   }

   public Player getPlayer() {
      return this.player;
   }

   public List<ItemStack> getInventory() {
      return this.inventory;
   }

   public int getExp() {
      return this.exp;
   }
}
