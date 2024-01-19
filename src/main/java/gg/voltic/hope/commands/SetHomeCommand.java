package gg.voltic.hope.commands;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player p)) {
         return true;
      } else {
         ConfigCursor cursor = new ConfigCursor(Hope.getInstance().getHomesFile(), "");
         cursor.set("homes." + p.getUniqueId() + ".home", LocationUtil.serialize(p.getLocation()));
         cursor.save();
         p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &a¡Casa guardada! &7Ahora puedes ir a ella usando &a&l/home&7."));
         return true;
      }
   }
}
