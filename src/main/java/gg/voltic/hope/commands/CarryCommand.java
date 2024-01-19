package gg.voltic.hope.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CarryCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player p)) {
         return true;
      } else {
         Player nearest = this.getNearestPlayer(p);
         if (nearest == null) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cAcércate más al jugador."));
            return true;
         } else {
            p.addPassenger(nearest);
            return true;
         }
      }
   }

   private Player getNearestPlayer(Player p) {
      double lowestDistanceSoFar = Double.MAX_VALUE;
      Player closestPlayer = null;

      for(Entity entity : p.getNearbyEntities(2.0, 1.0, 2.0)) {
         if (entity instanceof Player) {
            double distance = entity.getLocation().distance(p.getLocation());
            if (distance < lowestDistanceSoFar) {
               lowestDistanceSoFar = distance;
               closestPlayer = (Player)entity;
            }
         }
      }

      return closestPlayer;
   }
}
