package gg.voltic.hope.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
   public static String serialize(Location location) {
      return location.getWorld().getName()
         + ":"
         + location.getX()
         + ":"
         + location.getY()
         + ":"
         + location.getZ()
         + ":"
         + location.getYaw()
         + ":"
         + location.getPitch();
   }

   public static boolean sameLocation(Location block, Location player) {
      return new Region(block, player).isInRegion(player);
   }

   public static Location deserialize(String source) {
      String[] split = source.split(":");
      return new Location(
         Bukkit.getServer().getWorld(split[0]),
         Double.parseDouble(split[1]),
         Double.parseDouble(split[2]),
         Double.parseDouble(split[3]),
         Float.parseFloat(split[4]),
         Float.parseFloat(split[5])
      );
   }
}
