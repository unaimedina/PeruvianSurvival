package gg.voltic.hope.scenario;

import com.google.common.collect.Lists;
import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.ItemCreator;
import gg.voltic.hope.utils.menu.type.ChestMenu;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ScenarioMenu extends ChestMenu<Hope> {
    public ScenarioMenu() {
        super("Module Manager", 27);

        this.update();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        if (event.getSlot() >= 0 && event.getSlot() <= Hope.getInstance().getScenarioManager().getScenarios().size()) {
            Scenario module = Hope.getInstance().getScenarioManager().getScenarios().get(event.getSlot());
            List<String> scenariosInConfig = Hope.getInstance().getModulesFile().getConfig().getStringList("MODULES");
            FileConfig config = Hope.getInstance().getModulesFile();

            if (module.isEnabled()) {
                module.disable();
                event.getWhoClicked().sendMessage("§4[!] §cYou have disabled the " + module.getName() + " module.");
                scenariosInConfig.remove(module.getName());
                config.getConfig().set("MODULES", scenariosInConfig);
                config.save();

            } else {
                module.enable();
                event.getWhoClicked().sendMessage("§2[>] §aYou have enabled the " + module.getName() + " module.");
                scenariosInConfig.add(module.getName());
                config.getConfig().set("MODULES", scenariosInConfig);
                config.save();
            }

            update();
        }
    }

    private void update() {
        this.inventory.clear();

        for (Scenario module : Hope.getInstance().getScenarioManager().getScenarios()) {
            List<String> lore = Lists.newArrayList();

            lore.add("&7" + module.getDescription());
            lore.add(" ");
            lore.add((module.isEnabled() ? "&aClick to enable this module." : "&cClick to disable this module."));

            String color = module.isEnabled() ? "&a" : "&c";

            this.inventory.addItem(new ItemCreator(module.getMaterial()).setName(color + module.getName()).setLore(lore).get());
        }
    }
}
