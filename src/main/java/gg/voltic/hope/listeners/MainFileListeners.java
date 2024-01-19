package gg.voltic.hope.listeners;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.Common;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class MainFileListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Permission permission = Hope.getInstance().getPermission();

        if (!player.hasPlayedBefore()) {
            int random = (int) (Math.random() * 40.0) + 16;
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, random));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &a¡Bienvenido al survival! &7Has recibido &e" + random + " &7filetes."));
        }

        // player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', permission.getPrimaryGroup(player) + player.getName()));
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&a[+] &7El jugador &a" + player.getName() + " &7ha entrado al survival."));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (event.getReason() != PlayerQuitEvent.QuitReason.KICKED) {
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&c[-] &7El jugador &c" + player.getName() + " &7ha salido del survival."));
        } else {
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&c[-] &7El jugador &c" + player.getName() + " &7ha sido expulsado del survival."));
        }
    }

    @EventHandler
    public void onCommandBlocked(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().replaceAll("/", "").split(" ")[0];
        if (Hope.getInstance().getCommands().contains(command)) {
            event.setCancelled(true);
            event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', "&cYou have been banned."));
            Common.warn(false, command, event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onCommandBlocked(ServerCommandEvent event) {
        String command = event.getCommand().replaceAll("/", "").split(" ")[0];
        if (Hope.getInstance().getCommands().contains(command)) {
            event.setCommand(" ");
            event.setCancelled(true);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cu r cunt"));
            Common.warn(true, command);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Permission permission = Hope.getInstance().getPermission();
        String formatStr = permission.getPrimaryGroup(player) + player.getName() + "&7: &f";

        // player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', permission.getPrimaryGroup(player) + player.getName()));
        // event.setFormat(ChatColor.translateAlternateColorCodes('&', formatStr) + event.getMessage());
    }
}
