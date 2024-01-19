package gg.voltic.hope.scenario;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.FileConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings({"deprecation"})
@Getter
@Setter
public abstract class Scenario implements Listener {
   private final String name;
   private final String description;
   private boolean enabled;
   private FileConfig config;

   public Scenario(String name, String description, FileConfig config) {
      this.name = name;
      this.description = description;
      this.enabled = false;
      this.config = config;
   }

   public void enable() {
      Bukkit.getPluginManager().registerEvents(this, Hope.getInstance());
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[+] &7" + this.name + " loaded."));
      this.enabled = true;
   }

   public void disable() {
      HandlerList.unregisterAll(this);
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[-] &7" + this.name + " unloaded."));
      this.enabled = false;
   }
}
