//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package gg.voltic.hope.commands;

import gg.voltic.hope.Hope;
import gg.voltic.hope.utils.FileConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HopeCommand implements CommandExecutor {
   public HopeCommand() {
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      switch (args.length) {
         case 0:
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[?] &a&lHope &7by &cAlejandro12120 &7(Forked by Unai)."));
            break;
         case 1:
            switch (args[0].toLowerCase()) {
               case "reload":
                  if (!sender.hasPermission("hope.reload")) {
                     return true;
                  }

                  Hope.getInstance().getScenarioManager().getScenarios().stream().filter((scenario) -> scenario.getConfig() != null).forEach((scenario) -> {
                     scenario.setConfig(new FileConfig(Hope.getInstance(), scenario.getConfig().getFileName()));
                  });
                  sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[>] &aHope &7has been reloaded!"));
            }
      }

      return true;
   }
}
