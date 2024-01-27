package gg.voltic.hope.menus;

import gg.voltic.hope.Hope;
import gg.voltic.hope.menus.impl.Configurations;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.menu.type.ChestMenu;
import gg.voltic.hope.utils.scoreboard.Scoreboard;
import gg.voltic.hope.utils.scoreboard.ScoreboardManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigurationMenu extends ChestMenu<Hope> {

    private final Player player;

    public ConfigurationMenu(Player player) {
        super("Configuration Menu", 9 * 3);
        this.player = player;

        this.update();
    }

    private void update() {
        this.inventory.clear();

        int i = 0;
        for (Configurations configuration : Configurations.values()) {
            this.inventory.setItem(i, new ItemCreator(configuration.getItem())
                    .setName(Hope.getInstance().getPlayersFile().getConfig()
                                    .getBoolean("PLAYERS." + this.player.getUniqueId() + "." + configuration.getUuid()) == true ?
                                        ChatColor.GREEN + configuration.getTitle() : ChatColor.RED + configuration.getTitle())
                    .hideEnchants()
                    .get()
            );
            i++;
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        if (event.getSlot() >= 0 && event.getSlot() <= Configurations.values().length) {
            Configurations configuration = Arrays.stream(Configurations.values()).toList().get(event.getSlot());

            Player player = (Player) event.getWhoClicked();
            FileConfig config = Hope.getInstance().getPlayersFile();
            Scoreboard assemble = Hope.getInstance().getScoreboard();

            if (config.getConfig().get("PLAYERS." + player.getUniqueId()) != null) {
                ConfigCursor cursor = new ConfigCursor(config, "PLAYERS." + player.getUniqueId());

                if (cursor.get(configuration.getUuid()) != null) {
                    boolean configValue = cursor.getBoolean(configuration.getUuid());

                    if (configValue) {
                        cursor.set(configuration.getUuid(), false);

                        if (assemble.getBoards().containsKey(player.getUniqueId())) {
                            assemble.getBoards().remove(player.getUniqueId());
                            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                        }
                    } else {
                        cursor.set(configuration.getUuid(), true);

                        assemble.getBoards().put(
                                player.getUniqueId(),
                                new ScoreboardManager(player, assemble)
                        );
                    }
                } else {
                    cursor.set(configuration.getUuid(), false);

                    if (assemble.getBoards().containsKey(player.getUniqueId())) {
                        assemble.getBoards().remove(player.getUniqueId());
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }
                }

                this.update();
                cursor.save();
            } else {
                player.sendMessage(Common.translate("&cCannot load your player profile, try to relog."));
            }
        }
    }
}
