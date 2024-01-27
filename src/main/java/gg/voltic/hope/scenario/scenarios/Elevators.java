package gg.voltic.hope.scenario.scenarios;

import com.comphenix.packetwrapper.PacketWrapper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.common.collect.Lists;
import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Elevators extends Scenario {

    private final ItemStack elevator = new ItemCreator(Material.TARGET, 2, 1)
            .setName(Common.translate("&eElevator"))
            .setLore(Arrays.asList("1", "2"))
            .get();

    public Elevators() {
        super("Elevators", "Integrates an elevator system using signs.", null, Material.BAMBOO_SIGN);

        NamespacedKey backpackKey = new NamespacedKey(Hope.getInstance(), "elevator");
        ShapedRecipe backpackRecipe = new ShapedRecipe(backpackKey, this.elevator);

        backpackRecipe.shape("RDR", "XIX", "TST");
        backpackRecipe.setIngredient('R', Material.REDSTONE);
        backpackRecipe.setIngredient('D', Material.DAYLIGHT_DETECTOR);
        backpackRecipe.setIngredient('X', Material.REPEATER);
        backpackRecipe.setIngredient('I', Material.IRON_BLOCK);
        backpackRecipe.setIngredient('S', Material.STICKY_PISTON);
        backpackRecipe.setIngredient('T', Material.REDSTONE_TORCH);
        Bukkit.addRecipe(backpackRecipe);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        Location location = blockBelow.getLocation();

        FileConfig elevatorsConfig = Hope.getInstance().getElevatorsFile();
        ConfigurationSection section = elevatorsConfig.getConfig().getConfigurationSection("ELEVATORS");
        assert section != null;

        if (blockBelow.getType() != Material.AIR && section.getStringList("locations").contains(LocationUtil.serialize(blockBelow.getLocation()))) {
            switch (Objects.requireNonNull(Common.elevatorDirection(location))) {
                case "down" -> {
                    TaskUtil.runLater(() -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
                    },5);
                    player.teleport(Common.getLowestBlock(location, player.getLocation()));
                }
                default -> {
                    player.sendMessage(Common.translate("&cLa dirección no es válida, direcciones válidas: up, down"));
                }
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        Location location = blockBelow.getLocation();

        FileConfig elevatorsConfig = Hope.getInstance().getElevatorsFile();
        ConfigurationSection section = elevatorsConfig.getConfig().getConfigurationSection("ELEVATORS");
        assert section != null;

        if (blockBelow.getType() != Material.AIR && section.getStringList("locations").contains(LocationUtil.serialize(blockBelow.getLocation()))) {
            switch (Objects.requireNonNull(Common.elevatorDirection(location))) {
                case "up" -> {
                    TaskUtil.runLater(() -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
                    },5);
                    player.teleport(Common.getHighestBock(location, player.getLocation()));
                }
                default -> {
                    player.sendMessage(Common.translate("&cLa dirección no es válida, direcciones válidas: up, down"));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() == this.elevator.getType() && event.getItemInHand().getItemMeta().getLore().equals(Arrays.asList("1", "2"))) {
            FileConfig config = Hope.getInstance().getElevatorsFile();
            ConfigurationSection elevatorsSection = config.getConfig().getConfigurationSection("ELEVATORS");

            if (elevatorsSection == null) {
                elevatorsSection = config.getConfig().createSection("ELEVATORS");
                List<String> elevatorsList = Lists.newArrayList();

                String locationString = LocationUtil.serialize(event.getBlockPlaced().getLocation());

                elevatorsList.add(locationString);

                elevatorsSection.set("locations", elevatorsList);
            } else {
                List<String> elevatorsList = elevatorsSection.getStringList("locations");
                String locationString = LocationUtil.serialize(event.getBlockPlaced().getLocation());

                elevatorsList.add(locationString);

                elevatorsSection.set("locations", elevatorsList);
            }

            config.save();
        }
    }
}
