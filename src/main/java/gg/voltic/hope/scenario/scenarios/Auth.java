package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class Auth extends Scenario {
   private final HashMap<String, String> auth = new HashMap<>();
   ConfigCursor cursor = new ConfigCursor(this.getConfig(), "");

   public Auth() {
      super("Auth", "Integrates an auth system using IPs and UUIDs.", new FileConfig(Hope.getInstance(), "auth.yml"), Material.BOOK);

      for(String uuid : this.cursor.getKeys("uuids")) {
         this.auth.put(uuid, this.cursor.getString("uuids." + uuid));
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onJoin(PlayerJoinEvent event) {
      String uuid = event.getPlayer().getUniqueId().toString();
      String ip = event.getPlayer().getAddress().getAddress().getHostAddress().replaceAll("/", "");
      if (!this.hasAuth(uuid)) {
         this.cursor.set("uuids." + uuid, ip);
         this.getConfig().save();
         this.auth.put(uuid, ip);
      } else if (!this.getIP(uuid).equalsIgnoreCase(ip)) {
         event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', "&4[!] &cHa habido un problema, contacta con &lUnai"));
      }
   }

   public boolean hasAuth(String uuid) {
      return this.auth.containsKey(uuid);
   }

   public String getIP(String uuid) {
      return !this.hasAuth(uuid) ? null : this.auth.get(uuid);
   }
}
