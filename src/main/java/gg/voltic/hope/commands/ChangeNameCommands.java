package gg.voltic.hope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangeNameCommands implements CommandExecutor {

    @Deprecated
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player p)) {
            return true;
        } else {
            if (strings.length == 0) {
                p.sendMessage("§cUso: /nick <nick>");
                return true;
            } else {
                String nick = String.join(" ", strings);
                p.setDisplayName(nick);
                p.setPlayerListName(nick);
                p.sendMessage("§aTu nick ha sido cambiado a " + nick);
            }
            return true;
        }
        }
    }
