package gg.voltic.hope.trades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Trade {
   private Player player1;
   private Player player2;
   private List<ItemStack> items1 = new ArrayList();
   private List<ItemStack> items2 = new ArrayList();
   private TradeMenu menu;

   public Trade(Player player1, Player player2) {
      this.player1 = player1;
      this.player2 = player2;
   }

   public void cancel() {
      this.player1.closeInventory();
      this.player2.closeInventory();
      this.items1.forEach(item -> this.player1.getInventory().addItem(new ItemStack[]{item}));
      this.items2.forEach(item -> this.player2.getInventory().addItem(new ItemStack[]{item}));
      this.player1.closeInventory();
      this.player2.closeInventory();
      this.player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl intercambio ha sido cancelado."));
      this.player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl intercambio ha sido cancelado."));
   }

   public void addItem(Player player, ItemStack item) {
      if (player.getUniqueId() == this.player1.getUniqueId()) {
         this.items1.add(item);
      } else if (player.getUniqueId() == this.player2.getUniqueId()) {
         this.items2.add(item);
      }
   }

   public void removeItem(Player player, ItemStack item) {
      if (player.getUniqueId() == this.player1.getUniqueId()) {
         this.items1.add(item);
      } else if (player.getUniqueId() == this.player2.getUniqueId()) {
         this.items2.add(item);
      }
   }

   public List<ItemStack> getItems(Player player) {
      if (player.getUniqueId() == this.player1.getUniqueId()) {
         return this.items1;
      } else {
         return player.getUniqueId() == this.player2.getUniqueId() ? this.items2 : Collections.emptyList();
      }
   }

   public List<ItemStack> getOtherItems(Player player) {
      if (player.getUniqueId() != this.player1.getUniqueId()) {
         return this.items1;
      } else {
         return player.getUniqueId() != this.player2.getUniqueId() ? this.items2 : Collections.emptyList();
      }
   }

   public Player getPlayer1() {
      return this.player1;
   }

   public Player getPlayer2() {
      return this.player2;
   }

   public List<ItemStack> getItems1() {
      return this.items1;
   }

   public List<ItemStack> getItems2() {
      return this.items2;
   }

   public TradeMenu getMenu() {
      return this.menu;
   }

   public void setPlayer1(Player player1) {
      this.player1 = player1;
   }

   public void setPlayer2(Player player2) {
      this.player2 = player2;
   }

   public void setItems1(List<ItemStack> items1) {
      this.items1 = items1;
   }

   public void setItems2(List<ItemStack> items2) {
      this.items2 = items2;
   }

   public void setMenu(TradeMenu menu) {
      this.menu = menu;
   }
}
