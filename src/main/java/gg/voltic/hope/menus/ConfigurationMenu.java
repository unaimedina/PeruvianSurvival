package gg.voltic.hope.menus;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.menu.type.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfigurationMenu extends ChestMenu<Hope> {

    public ConfigurationMenu(Player player) {
        super("Configuration Menu", 9 * 3);

        this.update();
    }

    private void update() {
        this.inventory.clear();

        /*int i = 0;
        for (Configurations configuration : Configurations.values()) {
            this.inventory.setItem(i, new ItemCreator(configuration.getItem())
                    .setName(configuration.getTitle())
                    .setLore(configuration.getDescription())
                    .get()
            );
            i++;
        }*/
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        /*if (event.getSlot() >= 0 && event.getSlot() <= Configurations.values().length) {
            Bukkit.getConsoleSender().sendMessage(event.getCursor().getType() + "");
            Configurations configuration = Configurations.getConfiguration(event.getCursor().getType());

            Player player = (Player) event.getWhoClicked();
            FileConfig config = Hope.getInstance().getPlayersFile();

            if (config.getConfig().get(String.valueOf(player.getUniqueId())) != null) {
                ConfigCursor cursor = new ConfigCursor(config, String.valueOf(player.getUniqueId()));

                if (cursor.get(configuration.getUuid()) != null) {
                    boolean configValue = cursor.getBoolean(configuration.getUuid());

                    cursor.set(configuration.getUuid(), !configValue);
                    Hope.getInstance().getScoreboardProvider().instancePlayer(player, !configValue);
                } else {
                    cursor.set(configuration.getUuid(), false);
                    Hope.getInstance().getScoreboardProvider().instancePlayer(player, false);
                }

                cursor.save();
            } else {
                player.sendMessage(Common.translate("&cCannot load your player profile, try to relog."));
            }
        }*/
    }
}
