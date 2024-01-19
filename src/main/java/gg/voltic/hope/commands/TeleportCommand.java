package gg.voltic.hope.commands;

import gg.voltic.hope.Hope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportCommand implements CommandExecutor {
   private final HashMap<Player, Player> pending = new HashMap();
   private final List<Player> sending = new ArrayList();

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else if (label.equalsIgnoreCase("tp")) {
         final Player p = (Player)sender;
         if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cEspecifica un jugador."));
            return true;
         } else if (args.length == 1) {
            final Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
               sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] Ese jugador está offline."));
               return true;
            } else if (this.sending.contains(p)) {
               sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cYa has enviado una solicitud de teletransporte, espera 20 segundos."));
               return true;
            } else {
               this.pending.put(t, p);
               this.sending.add(p);
               sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &aSe ha enviado una solicitud de teletransporte a &l" + t.getName()));
               String[] message = new String[]{
                  "&a" + p.getName() + " te ha enviado una solicitud de teletransporte.",
                  "&eEscribe &a/tpaccept &epara aceptar la solicitud. ",
                  "&eO &a/tpdeny &epara denegar la solicitud.",
                  "&cExpira en 20 segundos"
               };
               Arrays.stream(message).forEach(s -> t.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
               (new BukkitRunnable() {
                  public void run() {
                     if (TeleportCommand.this.pending.containsKey(t) || TeleportCommand.this.sending.contains(p)) {
                        TeleportCommand.this.pending.remove(t);
                        TeleportCommand.this.sending.remove(p);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cTu solicitud de teletransporte ha expirado."));
                     }
                  }
               }).runTaskLaterAsynchronously(Hope.getInstance(), 400L);
               return true;
            }
         } else {
            return true;
         }
      } else if (label.equalsIgnoreCase("tpaccept")) {
         Player p = (Player)sender;
         if (!this.pending.containsKey(p)) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo tienes ninguna solicitud de tp :("));
            return true;
         } else {
            Player t = this.pending.get(p);
            t.teleport(p);
            this.pending.remove(p);
            this.sending.remove(t);
            return true;
         }
      } else if (label.equalsIgnoreCase("tpdeny")) {
         Player p = (Player)sender;
         if (!this.pending.containsKey(p)) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo tienes ninguna solicitud de tp :("));
            return true;
         } else {
            Player t = this.pending.get(p);
            this.pending.remove(p);
            this.sending.remove(t);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHas denegado la solicitud de teletransporte a " + t.getName()));
            t.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHas sido denegado :("));
            return true;
         }
      } else {
         return true;
      }
   }
}
