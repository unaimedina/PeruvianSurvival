package gg.voltic.hope.commands;

import gg.voltic.hope.menus.ConfigurationMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            new ConfigurationMenu(player).open(player);
        } else {
            return true;
        }
        return false;
    }
}
