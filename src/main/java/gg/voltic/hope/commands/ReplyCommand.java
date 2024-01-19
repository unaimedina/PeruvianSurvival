package gg.voltic.hope.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!label.equalsIgnoreCase("r") && !label.equalsIgnoreCase("reply")) {
         return true;
      } else if (!(sender instanceof Player p)) {
         return true;
      } else {
         if (args.length == 0) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/" + label + " <message>"));
         } else if (PrivateMessageCommand.getLastMessage().containsKey(p)) {
            String tName = PrivateMessageCommand.getLastMessage().get(p).getName();
            StringBuilder sb = new StringBuilder();

            for(String arg : args) {
               sb.append(" ").append(arg);
            }

            String message = sb.substring(1);
            p.performCommand("msg " + tName + " " + message);
         } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cError, ¡el jugador '" + args[0] + "' está desconectado!"));
         }

         return true;
      }
   }
}
