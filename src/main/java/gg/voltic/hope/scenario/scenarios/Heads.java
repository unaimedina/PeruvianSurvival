package gg.voltic.hope.scenario.scenarios;

import gg.voltic.hope.Hope;
import gg.voltic.hope.scenario.Scenario;
import gg.voltic.hope.utils.Common;
import gg.voltic.hope.utils.FileConfig;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Heads extends Scenario {
   public Heads() {
      super("Heads", "Gives you a head when you kill a Skeleton, Creeper or Zombie.", new FileConfig(Hope.getInstance(), "heads.yml"), Material.ZOMBIE_HEAD);
   }

   @EventHandler
   public void onEntityKilled(EntityDeathEvent event) {
      LivingEntity entity = event.getEntity();
      FileConfiguration config = this.getConfig().getConfig();
      int chance = (100 - config.getInt("CHANCE"));
      if (Common.getRandom(100) >= chance) {
         switch(entity.getType()) {
            case ZOMBIE:
               event.getDrops().add(new ItemStack(Material.ZOMBIE_HEAD));
               break;
            case SKELETON:
               event.getDrops().add(new ItemStack(Material.SKELETON_SKULL));
               break;
            case CREEPER:
               event.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
         }
      }
   }
}
