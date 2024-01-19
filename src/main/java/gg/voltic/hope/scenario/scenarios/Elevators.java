package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.Common;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Elevators extends Scenario {
   public Elevators() {
      super("Elevators", "Integrates an elevator system using signs.", null);
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (event.getClickedBlock().getState() instanceof Sign sign) {
            String firstLine = ChatColor.stripColor(sign.getLine(0));
            if (firstLine.equalsIgnoreCase("[Elevator]")) {
               String direction = ChatColor.stripColor(sign.getLine(1)).toLowerCase();
               Player player = event.getPlayer();
               switch(direction) {
                  case "up":
                     player.teleport(Common.getHighestBock(sign.getLocation(), player.getLocation()));
                     break;
                  case "down":
                     player.teleport(Common.getLowestBlock(sign.getLocation(), player.getLocation()));
                     break;
                  default:
                     player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLa dirección no es válida, direcciones válidas: up, down"));
               }
            }
         }
      }
   }

   @EventHandler
   public void onElevatorPlace(SignChangeEvent event) {
      String[] lines = event.getLines();
      if (lines[0].equalsIgnoreCase("Elevator")) {
         event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&a[Elevator]"));
         event.setLine(1, ChatColor.translateAlternateColorCodes('&', "&l" + WordUtils.capitalize(lines[1].toLowerCase())));
      }
   }
}
