package gg.voltic.hope.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PrivateMessageCommand implements CommandExecutor {
   private static final HashMap<Player, Player> lastMessage = new HashMap();

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!label.equalsIgnoreCase("msg") && !label.equalsIgnoreCase("tell") && !label.equalsIgnoreCase("message")) {
         return true;
      } else if (!(sender instanceof Player p)) {
         return true;
      } else {
         if (args.length < 2) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c/" + label + " <targetName> <message>"));
            return true;
         } else if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cError, ¡el jugador '" + args[0] + "' está desconectado!"));
            return true;
         } else {
            Player t = Bukkit.getPlayer(args[0]);
            StringBuilder sb = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               sb.append(" ").append(args[i]);
            }

            String message = sb.substring(1);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(&6" + p.getName() + " &a-> &6" + t.getName() + "&7): &f" + message));
            t.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(&6" + p.getName() + " &c-> &6" + t.getName() + "&7): &f" + message));
            lastMessage.put(t, p);
            lastMessage.put(p, t);
            return true;
         }
      }
   }

   public static HashMap<Player, Player> getLastMessage() {
      return lastMessage;
   }
}
