package gg.voltic.hope.scenario.disabledScenarios;

import gg.voltic.hope.scenario.Scenario;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;

public class Debug extends Scenario {
   public Debug() {
      super("Debug", "Simple debug", null);
   }

   @EventHandler
   public void onChat(PlayerChatEvent event) {
      String var2 = event.getMessage().toLowerCase();
      byte var3 = -1;
      switch(var2.hashCode()) {
         case 97:
            if (var2.equals("a")) {
               var3 = 0;
            }
         default:
            switch(var3) {
               case 0:
                  this.spawnChest(event.getPlayer().getLocation());
            }
      }
   }

   private void spawnChest(Location location) {
   }
}
