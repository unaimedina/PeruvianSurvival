package gg.voltic.hope.commands;

import gg.voltic.hope.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!(sender instanceof Player player)) {
                    return true;
                }

                player.sendMessage(
                        ChatColor.translateAlternateColorCodes(
                                '&', "&6[!] &7Has estado jugando durante: &a" + Common.getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20)
                        )
                );
                break;
            case 1:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (offlinePlayer.hasPlayedBefore()) {
                        sender.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                        '&',
                                        "&6[!] &7El jugador &a"
                                                + offlinePlayer.getName()
                                                + " &7ha jugado durante: &a"
                                                + Common.getTime(offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20)
                                )

                        );
                        return true;
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[!] &c¡El jugador &4" + offlinePlayer.getName() + " &cno ha jugado nunca!"));
                    return true;
                }

                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes(
                                '&', "&6[!] &7El jugador &a" + target.getName() + " &7ha jugado durante: &a" + Common.getTime(target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20)
                        )
                );
        }

        return true;
    }
}
