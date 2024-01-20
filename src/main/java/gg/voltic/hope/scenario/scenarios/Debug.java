package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.scenario.Scenario;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Debug extends Scenario {
   public Debug() {
      super("Debug", "Simple debug", null, Material.PAPER);
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      String var2 = event.getMessage().toLowerCase();
       if (var2.equals("test")) {
           this.spawnChest(event.getPlayer().getLocation());
       }
   }

   private void spawnChest(Location location) {
   }
}
