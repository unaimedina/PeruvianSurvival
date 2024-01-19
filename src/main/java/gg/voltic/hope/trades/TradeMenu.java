package gg.voltic.hope.trades;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.menu.type.ChestMenu;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeMenu extends ChestMenu<Hope> {
   private final Trade trade;
   private final List<ItemStack> forbidden = new ArrayList();
   private final Player currentPlayer;

   public TradeMenu(Trade trade, Player currentPlayer) {
      super(trade.getPlayer1().getName() + " » " + trade.getPlayer2().getName(), 54);
      this.trade = trade;
      this.currentPlayer = currentPlayer;
   }

   private void update() {
      this.inventory.clear();

      for(int i = 0; i < 9; ++i) {
         this.inventory.setItem(i, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      }

      for(int i = 45; i < 54; ++i) {
         if (i != 48 && i != 50) {
            this.inventory.setItem(i, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
         }
      }

      for(int i = 9; i <= 36; ++i) {
         if (i % 9 == 0) {
            this.inventory.setItem(i, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
         }
      }

      for(int i = 17; i <= 44; ++i) {
         if (i + 1 == 0) {
            this.inventory.setItem(i, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
         }
      }

      this.inventory.setItem(13, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(22, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(31, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(39, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(40, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(41, new ItemCreator(Material.BLACK_STAINED_GLASS).setName(" ").get());
      this.inventory.setItem(48, new ItemCreator(Material.RED_WOOL).setName("&cCancelar").get());
      this.inventory.setItem(50, new ItemCreator(Material.GREEN_WOOL).setName("&aAceptar").get());

      for(ItemStack content : this.inventory.getContents()) {
         if (content.getType() != Material.AIR) {
            this.forbidden.add(content);
         }
      }

      int[] playerSlots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38};

      for(int i = 0; i < this.trade.getItems(this.currentPlayer).size(); ++i) {
         if (i <= playerSlots.length) {
            this.inventory.setItem(playerSlots[i], this.trade.getItems(this.currentPlayer).get(i));
         }
      }

      int[] otherSlots = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34, 42, 43};

      for(int i = 0; i < this.trade.getOtherItems(this.currentPlayer).size(); ++i) {
         if (i <= otherSlots.length) {
            this.inventory.setItem(otherSlots[i], this.trade.getOtherItems(this.currentPlayer).get(i));
         }
      }
   }

   @Override
   public void onInventoryClick(InventoryClickEvent event) {
      Inventory clickedInventory = event.getClickedInventory();
      Inventory topInventory = event.getView().getTopInventory();
      if (topInventory.equals(this.inventory)) {
         if (topInventory.equals(clickedInventory)) {
            if (this.forbidden.contains(event.getCurrentItem())) {
               event.setCancelled(true);
            }

            if (event.getCurrentItem().getType() == Material.RED_WOOL
               && event.getCurrentItem().getItemMeta().hasDisplayName()
               && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cCancelar"))) {
               this.trade.cancel();
            }
         }
      }
   }

   @Override
   public void onInventoryMove(InventoryMoveItemEvent event) {
      if (event.getDestination().equals(this.inventory)) {
         this.trade.addItem(this.currentPlayer, event.getItem());
         this.update();
      } else if (event.getSource().equals(this.inventory)) {
         this.trade.removeItem(this.currentPlayer, event.getItem());
         this.update();
      }
   }
}
