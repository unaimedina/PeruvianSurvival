package gg.voltic.hope.providers;

import com.google.common.collect.Lists;
import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.scoreboard.ScoreboardAdapter;
import gg.voltic.hope.utils.scoreboard.ScoreboardStyle;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ScoreboardProvider implements ScoreboardAdapter {

    private int phase = 0;

    public ScoreboardProvider() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (phase < 1) {
                    phase++;
                } else {
                    phase = 0;
                }
            }
        }.runTaskTimerAsynchronously(Hope.getInstance(), 0, 10 * 20);
    }

    @Override
    public String getTitle(Player player) {
        return Common.translate("&6Calvo perro");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = Lists.newArrayList();

        double tps = Bukkit.getServer().getTPS()[0];

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedTPS = decimalFormat.format(tps);

        switch (phase) {
            case 0 -> {
                lines.add("&7" + this.dateHour());
                lines.add("");
                lines.add("&eStatistics");
                lines.add(" &7* &fDeaths&7: &6" + Common.getDeaths(player));
                lines.add(" &7* &fMobs Killed&7: &6" + Common.getMobKills(player));
                lines.add(" &7* &fPlayed Time&7: &6" + Common.getPlayTime(player));
                lines.add(" ");
                lines.add("&eServer");
                lines.add(" &7* &fMemory&7: &6" + Common.getMemory());
                lines.add(" &7* &fTPS&7: &6" + formattedTPS);
                lines.add("");
                lines.add("&6mc.bufas.cat");
            }
            case 1 -> {
                lines.add("&7" + this.dateHour());
                lines.add("");
                lines.add("&eDeaths: ");
                for (Player online : Bukkit.getOnlinePlayers()) {
                    lines.add(" &7* &f" + online.getName() + "&7: &6" + Common.getDeaths(online));
                }
                lines.add(" ");
                lines.add("&eServer");
                lines.add(" &7* &fMemory&7: &6" + Common.getMemory());
                lines.add(" &7* &fTPS&7: &6" + formattedTPS);
                lines.add("");
                lines.add("&6mc.bufas.cat");
            }
        }

        return Common.translate(lines);
    }

    @Override
    public ScoreboardStyle getBoardStyle(Player player) {
        return ScoreboardStyle.MODERN;
    }

    private String dateHour() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Madrid/Europe"));
        return dateFormat.format(now);
    }
}
