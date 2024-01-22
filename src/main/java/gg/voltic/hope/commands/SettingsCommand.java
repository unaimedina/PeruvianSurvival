package gg.voltic.hope.commands;

import gg.voltic.hope.menus.ConfigurationMenu;
import gg.voltic.hope.scenario.ScenarioMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if ((sender instanceof Player player)) {
            ConfigurationMenu menu = new ConfigurationMenu(player);

            menu.open(player);
            player.playSound(player.getLocation(), "ui.button.click", 1, 1);
            return true;
        } else {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }
    }
}
