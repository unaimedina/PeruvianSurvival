package gg.voltic.hope.utils;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

public class Cooldown {
   private UUID uniqueId = UUID.randomUUID();
   private long start = System.currentTimeMillis();
   private long expire;
   private boolean notified;

   public Cooldown(long duration) {
      this.expire = this.start + duration;
      if (duration == 0L) {
         this.notified = true;
      }
   }

   public long getPassed() {
      return System.currentTimeMillis() - this.start;
   }

   public long getRemaining() {
      return this.expire - System.currentTimeMillis();
   }

   public boolean hasExpired() {
      return System.currentTimeMillis() - this.expire >= 0L;
   }

   public String getTimeLeft() {
      return this.getRemaining() >= 60000L ? this.millisToRoundedTime(this.getRemaining()) : this.millisToSeconds(this.getRemaining());
   }

   private String millisToRoundedTime(long millis) {
      ++millis;
      long seconds = millis / 1000L;
      long minutes = seconds / 60L;
      long hours = minutes / 60L;
      long days = hours / 24L;
      long weeks = days / 7L;
      long months = weeks / 4L;
      long years = months / 12L;
      ++minutes;
      if (years > 0L) {
         return years + " año" + (years == 1L ? "" : "s");
      } else if (months > 0L) {
         return months + " mes" + (months == 1L ? "" : "s");
      } else if (weeks > 0L) {
         return weeks + " semana" + (weeks == 1L ? "" : "s");
      } else if (days > 0L) {
         return days + " día" + (days == 1L ? "" : "s");
      } else if (hours > 0L) {
         return hours + " hora" + (hours == 1L ? "" : "s");
      } else {
         return minutes > 0L ? minutes + " minuto" + (minutes == 1L ? "" : "s") : seconds + " segundo" + (seconds == 1L ? "" : "s");
      }
   }

   private String millisToSeconds(long millis) {
      return new DecimalFormat("#0.0").format((float)millis / 1000.0F);
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public long getStart() {
      return this.start;
   }

   public long getExpire() {
      return this.expire;
   }

   public boolean isNotified() {
      return this.notified;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }

   public void setStart(long start) {
      this.start = start;
   }

   public void setExpire(long expire) {
      this.expire = expire;
   }

   public void setNotified(boolean notified) {
      this.notified = notified;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Cooldown other)) {
         return false;
      } else {
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getStart() != other.getStart()) {
            return false;
         } else if (this.getExpire() != other.getExpire()) {
            return false;
         } else if (this.isNotified() != other.isNotified()) {
            return false;
         } else {
            Object this$uniqueId = this.getUniqueId();
            Object other$uniqueId = other.getUniqueId();
            return Objects.equals(this$uniqueId, other$uniqueId);
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Cooldown;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $start = this.getStart();
      result = result * 59 + (int)($start >>> 32 ^ $start);
      long $expire = this.getExpire();
      result = result * 59 + (int)($expire >>> 32 ^ $expire);
      result = result * 59 + (this.isNotified() ? 79 : 97);
      Object $uniqueId = this.getUniqueId();
      return result * 59 + ($uniqueId == null ? 43 : $uniqueId.hashCode());
   }

   @Override
   public String toString() {
      return "Cooldown(uniqueId="
         + this.getUniqueId()
         + ", start="
         + this.getStart()
         + ", expire="
         + this.getExpire()
         + ", notified="
         + this.isNotified()
         + ")";
   }
}
