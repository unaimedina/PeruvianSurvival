package gg.voltic.hope.commands;

import gg.voltic.hope.Hope;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class MlgCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p) {
            Location loc = p.getLocation();
            Bukkit.broadcastMessage("§6[Survival] §7¡§2" + p.getName() + " §ava a intentar un WaterDrop§7!");
            for (int i = 10; i > 0; i--) {
                int finalI = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendTitle("§2" + finalI, "§asegundos antes del tp", 0, 20, 0);
                    }
                }.runTaskLater(Hope.getInstance(), (10 - i) * 20L);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location locs = new Location(loc.getWorld(), loc.getX(), loc.getY() + 350, loc.getZ());
                    p.teleport(locs);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.playSound(locs, Sound.ENTITY_TNT_PRIMED,1,1);
                        }
                    }.runTaskLater(Hope.getInstance(), 10L);

                }
            }.runTaskLater(Hope.getInstance(), 200L);
        }
        return true;
    }
}
