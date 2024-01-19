package gg.voltic.hope.utils;

import gg.voltic.hope.Hope;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
   public static void run(Runnable runnable) {
      Hope.getInstance().getServer().getScheduler().runTask(Hope.getInstance(), runnable);
   }

   public static void runTimer(Runnable runnable, long delay, long timer) {
      Hope.getInstance().getServer().getScheduler().runTaskTimer(Hope.getInstance(), runnable, delay, timer);
   }

   public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
      runnable.runTaskTimer(Hope.getInstance(), delay, timer);
   }

   public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
      runnable.runTaskTimerAsynchronously(Hope.getInstance(), delay, timer);
   }

   public static void runLater(Runnable runnable, long delay) {
      Hope.getInstance().getServer().getScheduler().runTaskLater(Hope.getInstance(), runnable, delay);
   }

   public static void runLaterAsync(Runnable runnable, long delay) {
      Hope.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Hope.getInstance(), runnable, delay);
   }

   public static void runAsync(Runnable runnable) {
      Hope.getInstance().getServer().getScheduler().runTaskAsynchronously(Hope.getInstance(), runnable);
   }
}
