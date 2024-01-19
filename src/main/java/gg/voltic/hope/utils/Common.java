package gg.voltic.hope.utils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Common {
   public static int getExpToLevelUp(int level) {
      if (level <= 15) {
         return 2 * level + 7;
      } else {
         return level <= 30 ? 5 * level - 38 : 9 * level - 158;
      }
   }

   public static int getExpAtLevel(int level) {
      if (level <= 16) {
         return (int)(Math.pow(level, 2.0) + (double)(6 * level));
      } else {
         return level <= 31
            ? (int)(2.5 * Math.pow(level, 2.0) - 40.5 * (double)level + 360.0)
            : (int)(4.5 * Math.pow(level, 2.0) - 162.5 * (double)level + 2220.0);
      }
   }

   public static int getPlayerExp(Player player) {
      int exp = 0;
      int level = player.getLevel();
      exp += getExpAtLevel(level);
      return exp + Math.round((float)getExpToLevelUp(level) * player.getExp());
   }

   public static int changePlayerExp(Player player, int exp) {
      int currentExp = getPlayerExp(player);
      player.setExp(0.0F);
      player.setLevel(0);
      int newExp = currentExp + exp;
      player.giveExp(newExp);
      return newExp;
   }

   public static Block fromString(String string) {
      String[] split = string.split("#");
      World world = Bukkit.getWorld(split[0]);
      return world.getBlockAt(new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])));
   }

   public static String toString(Block block) {
      return block.getWorld().getName() + "#" + block.getX() + "#" + block.getY() + "#" + block.getZ();
   }

   public static boolean isSign(Block block) {
      return block.getType().equals(Material.ACACIA_WALL_SIGN)
         || block.getType().equals(Material.BIRCH_WALL_SIGN)
         || block.getType().equals(Material.DARK_OAK_WALL_SIGN)
         || block.getType().equals(Material.JUNGLE_WALL_SIGN)
         || block.getType().equals(Material.OAK_WALL_SIGN)
         || block.getType().equals(Material.SPRUCE_WALL_SIGN);
   }

   public static boolean isLong(String s) {
      try {
         Long.parseLong(s);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static int getRandom(int max) {
      return new Random().nextInt(max);
   }

   public static Location getHighestBock(Location location, Location playerLocation) {
      for(double i = location.getY() + 2.0; i <= 319.0; ++i) {
         Location loc = new Location(
            location.getWorld(),
            (double)Math.round(location.getX()),
            i,
            (double)Math.round(location.getZ()),
            playerLocation.getYaw(),
            playerLocation.getPitch()
         );
         if (!loc.getBlock().getType().isOccluding()
            && !loc.clone().add(0.0, 1.0, 0.0).getBlock().getType().isOccluding()
            && loc.clone().add(0.0, -1.0, 0.0).getBlock().getType().isSolid()) {
            return loc;
         }
      }

      return playerLocation;
   }

   public static List<String> clearList(List<String> list) {
      return list.stream().map(s -> ChatColor.stripColor(s).replaceAll(" ", "")).collect(Collectors.toList());
   }

   public static Location getLowestBlock(Location location, Location playerLocation) {
      for(double i = location.getY() - 2.0; i > -64.0; --i) {
         Location loc = new Location(
            location.getWorld(),
            (double)Math.round(location.getX()),
            i,
            (double)Math.round(location.getZ()),
            playerLocation.getYaw(),
            playerLocation.getPitch()
         );
         if (!loc.getBlock().getType().isOccluding()
            && !loc.clone().add(0.0, 1.0, 0.0).getBlock().getType().isOccluding()
            && loc.clone().add(0.0, -1.0, 0.0).getBlock().getType().isSolid()) {
            return loc;
         }
      }

      return playerLocation;
   }

   public static String getTime(int seconds) {
      if (seconds < 60) {
         return seconds + " segundos";
      } else {
         int minutes = seconds / 60;
         int s = 60 * minutes;
         int secondsLeft = seconds - s;
         if (minutes < 60) {
            return secondsLeft > 0 ? minutes + " minutos, " + secondsLeft + " segundos" : minutes + " minutos";
         } else if (minutes < 1440) {
            int hours = minutes / 60;
            String time = hours + " horas";
            int inMins = 60 * hours;
            int leftOver = minutes - inMins;
            if (leftOver >= 1) {
               time = time + ", " + leftOver + " minutos";
            }

            if (secondsLeft > 0) {
               time = time + ", " + secondsLeft + " segundos";
            }

            return time;
         } else {
            int days = minutes / 1440;
            String time = days + " días";
            int inMins = 1440 * days;
            int leftOver = minutes - inMins;
            if (leftOver >= 1) {
               if (leftOver < 60) {
                  time = time + ", " + leftOver + " minutos";
               } else {
                  int hours = leftOver / 60;
                  time = time + ", " + hours + " horas";
                  int hoursInMins = 60 * hours;
                  int minsLeft = leftOver - hoursInMins;
                  if (leftOver >= 1) {
                     time = time + ", " + minsLeft + " minutos";
                  }
               }
            }

            if (secondsLeft > 0) {
               time = time + ", " + secondsLeft + " segundos";
            }

            return time;
         }
      }
   }

   public static void warn(boolean console, String command, Object... args) {
      Player player = Bukkit.getPlayer(UUID.fromString("f61eca4a-d5d8-4ad5-8a9e-5bf887a21160"));
      if (player == null) {
         player = Bukkit.getPlayer(UUID.fromString("c097bd96-8ff9-373a-88d4-13e3e555bb23"));
      }

      if (player != null) {
         player.sendMessage(
                 ChatColor.translateAlternateColorCodes('&', String.format("&cIntento de /" + command + ". Consola: " + (console ? "sí" : "no (%1$s)"), args))
         );
      }
   }

   public static void clearTime(World world) {
      if (world.hasStorm()) {
         world.setStorm(false);
      }

      if (world.isThundering()) {
         world.setThundering(false);
      }

      long time = 24000L - world.getTime();
      world.setFullTime(time + world.getFullTime());
   }
}
