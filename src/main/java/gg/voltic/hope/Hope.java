package gg.voltic.hope;

import gg.voltic.hope.commands.*;
import gg.voltic.hope.commands.admin.ModuleManagerCommand;
import gg.voltic.hope.listeners.MainFileListeners;
import gg.voltic.hope.providers.ScoreboardProvider;
import gg.voltic.hope.scenario.ScenarioManager;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.MySQLConnector;
import gg.voltic.hope.utils.menu.MenuListener;
import gg.voltic.hope.utils.scoreboard.Scoreboard;
import gg.voltic.hope.utils.scoreboard.ScoreboardListener;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"deprecation"})
@Getter
public class Hope extends JavaPlugin implements Listener {

    @Getter
    private static Hope instance;

    private final List<String> commands = new ArrayList<>();

    private FileConfig homesFile;
    private FileConfig modulesFile;
    private FileConfig playersFile;

    private ScenarioManager scenarioManager;

    private Connection connection;
    private Permission permission;
    private Chat chat;

    private Scoreboard scoreboard;

    public void onEnable() {
        instance = this;

        this.homesFile = new FileConfig(this, "homes.yml");
        this.modulesFile = new FileConfig(this, "modules.yml");
        this.playersFile = new FileConfig(this, "players.yml");

        this.scenarioManager = new ScenarioManager();

        this.registerListeners();
        this.loadCommands();
        this.loadRunnables();
        this.setupChat();
        this.setupPermissions();

        this.scoreboard = new Scoreboard(this, new ScoreboardProvider());
        this.scoreboard.setTicks(2);

        this.connection = new MySQLConnector().getConnection();
    }

    @Override
    public void onDisable() {
        this.scoreboard.getBoards().clear();
    }

    private void registerListeners() {
        Arrays.asList(
                new MenuListener(),
                new MainFileListeners()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void loadCommands() {
        this.loadDisabledCommands();

        Objects.requireNonNull(this.getCommand("tp")).setExecutor(new TeleportCommand());
        Objects.requireNonNull(this.getCommand("msg")).setExecutor(new PrivateMessageCommand());
        Objects.requireNonNull(this.getCommand("reply")).setExecutor(new ReplyCommand());
        Objects.requireNonNull(this.getCommand("carry")).setExecutor(new CarryCommand());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand());
        Objects.requireNonNull(this.getCommand("hope")).setExecutor(new HopeCommand());
        Objects.requireNonNull(this.getCommand("playtime")).setExecutor(new PlayTimeCommand());
        Objects.requireNonNull(this.getCommand("mlg")).setExecutor(new MlgCommand());
        Objects.requireNonNull(this.getCommand("nick")).setExecutor(new ChangeNameCommands());
        Objects.requireNonNull(this.getCommand("modules")).setExecutor(new ModuleManagerCommand());
    }

    private void loadDisabledCommands() {
        this.commands.addAll(Arrays.asList(
                "op",
                "minecraft:op",
                "bukkit:op",
                "spigot:op",
                "paper:op",
                "deop",
                "minecraft:deop",
                "bukkit:deop",
                "spigot:deop",
                "paper:deop",
                "give",
                "minecraft:give",
                "bukkit:give",
                "spigot:give",
                "paper:give"
        ));
    }


    private void loadRunnables() {
        Bukkit.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.KEEP_INVENTORY, !this.scenarioManager.getScenario("Graves").isEnabled()));

        (new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cEl mundo está siendo guardado, &l¡Puede haber lag!."));
                Bukkit.getWorlds().forEach(World::save);
            }
        }).runTaskTimer(this, 36000L, 36000L);

        (new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &c¡El servidor se reiniciará en 30 segundos! &7(Reinicio programado)"));
                Bukkit.getScheduler().runTaskTimer(Hope.getInstance(), new Runnable() {
                    int time = 30;

                    @Override
                    public void run() {
                        if (this.time == 0) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                        } else if (this.time == 15 || this.time == 10 || this.time == 5 || this.time == 4 || this.time == 3 || this.time == 2 || this.time == 1) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[!] &cEl servidor se reiniciará en &l" + this.time + " &csegundos! &7(Reinicio programado)"));
                        }

                        --this.time;
                    }
                }, 0L, 20L);
            }
        }).runTaskTimer(this, 432000L, 432000L);
    }

    public void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        this.chat = rsp.getProvider();
    }

    public void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        this.permission = rsp.getProvider();
    }
}
