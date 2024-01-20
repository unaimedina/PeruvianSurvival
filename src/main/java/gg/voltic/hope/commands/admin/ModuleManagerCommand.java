package gg.voltic.hope.commands.admin;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.scenario.ScenarioMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModuleManagerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {

        if ((commandSender instanceof Player player)) {
            if (!player.hasPermission("hope.admin")) {
                player.sendMessage("§4[!] §cNo tienes permisos para ejecutar este comando.");
                return true;
            } else {
                ScenarioMenu menu = new ScenarioMenu();
                player.sendMessage("§2[>] §aAbriendo el menú de administración de módulos.");

                menu.open(player);
                player.playSound(player.getLocation(), "ui.button.click", 1, 1);
                return true;
            }
        } else {
            commandSender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }
    }
}
