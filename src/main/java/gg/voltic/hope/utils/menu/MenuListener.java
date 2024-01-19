package gg.voltic.hope.utils.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class MenuListener implements Listener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory().getHolder() instanceof Menu) {
         ((Menu)event.getInventory().getHolder()).onInventoryClick(event);
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent event) {
      if (event.getInventory().getHolder() instanceof Menu) {
         ((Menu)event.getInventory().getHolder()).onInventoryDrag(event);
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent event) {
      if (event.getInventory().getHolder() instanceof Menu) {
         ((Menu)event.getInventory().getHolder()).onInventoryClose(event);
      }
   }

   @EventHandler
   public void onInventoryMoveItem(InventoryMoveItemEvent event) {
      if (event.getDestination().getHolder() instanceof Menu) {
         ((Menu)event.getDestination().getHolder()).onInventoryMove(event);
      } else if (event.getSource().getHolder() instanceof Menu) {
         ((Menu)event.getSource().getHolder()).onInventoryMove(event);
      }
   }
}
