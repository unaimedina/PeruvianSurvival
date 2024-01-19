package gg.voltic.hope.commands;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player p)) {
         return true;
      } else {
         ConfigCursor cursor = new ConfigCursor(Hope.getInstance().getHomesFile(), "");
         if (cursor.exists("homes." + p.getUniqueId() + ".home")) {
            Location home = LocationUtil.deserialize(cursor.getString("homes." + p.getUniqueId() + ".home"));
            p.teleport(home);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &a¡Has vuelto a tu casa!"));
         } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cTu casa no se ha encontrado, ve a tu casa y pon: /sethome."));
         }

         return true;
      }
   }
}
