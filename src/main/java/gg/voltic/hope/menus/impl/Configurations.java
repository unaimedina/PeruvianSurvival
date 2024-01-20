package gg.voltic.hope.menus.impl;

import gg.voltic.hope.utils.Common;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Configurations {
    SCOREBOARD(Common.translate("Scoreboard Visibility"), Common.translate(Arrays.asList("", "")), Material.CLOCK, ConfigurationTypes.BOOLEAN, "board");

    String title;
    List<String> description;
    Material item;
    ConfigurationTypes type;
    String uuid;

    Configurations(String title, List<String> description, Material item, ConfigurationTypes type, String uuid) {
        this.title = title;
        this.description = description;
        this.item = item;
        this.type = type;
        this.uuid = uuid;
    }

    public static Configurations getConfiguration(Material material) {
        return Arrays.stream(values()).filter(configuration -> configuration.item == material).findAny().orElse(null);
    }
}
