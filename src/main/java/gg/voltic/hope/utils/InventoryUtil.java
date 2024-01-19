package gg.voltic.hope.utils;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtil {
   public static ItemStack[] deepClone(ItemStack[] origin) {
      Preconditions.checkNotNull(origin, "Origin cannot be null");
      ItemStack[] cloned = new ItemStack[origin.length];

      for(int i = 0; i < origin.length; ++i) {
         ItemStack next = origin[i];
         cloned[i] = next == null ? null : next.clone();
      }

      return cloned;
   }

   public static int getSafestInventorySize(int initialSize) {
      return (initialSize + 8) / 9 * 9;
   }

   public static int repairItem(ItemStack item) {
      if (item == null) {
         return 0;
      } else {
         Material material = item.getType();
         if (material.isBlock() || material.getMaxDurability() < 1) {
            return 0;
         } else if (item.getDurability() <= 0) {
            return 0;
         } else {
            item.setDurability((short)0);
            return 1;
         }
      }
   }

   public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
      ItemStack[] contents = inventory.getContents();
      boolean compareDamage = type.getMaxDurability() == 0;

      for(int i = quantity; i > 0; --i) {
         for(ItemStack content : contents) {
            if (content != null && content.getType() == type && (!compareDamage || content.getData().getData() == data)) {
               if (content.getAmount() <= 1) {
                  inventory.removeItem(content);
               } else {
                  content.setAmount(content.getAmount() - 1);
               }
               break;
            }
         }
      }
   }

   public static int countAmount(Inventory inventory, Material type, short data) {
      ItemStack[] contents = inventory.getContents();
      boolean compareDamage = type.getMaxDurability() == 0;
      int counter = 0;

      for(ItemStack item : contents) {
         if (item != null && item.getType() == type && (!compareDamage || item.getData().getData() == data)) {
            counter += item.getAmount();
         }
      }

      return counter;
   }

   public static boolean isEmpty(Inventory inventory) {
      return isEmpty(inventory, true);
   }

   public static boolean isEmpty(Inventory inventory, boolean checkArmour) {
      boolean result = true;

      for(ItemStack content : inventory.getContents()) {
         if (content != null && content.getType() != Material.AIR) {
            result = false;
            break;
         }
      }

      if (!result) {
         return false;
      } else {
         if (checkArmour && inventory instanceof PlayerInventory) {
            for(ItemStack content : ((PlayerInventory)inventory).getArmorContents()) {
               if (content != null && content.getType() != Material.AIR) {
                  result = false;
                  break;
               }
            }
         }

         return result;
      }
   }

   public static boolean clickedTopInventory(InventoryDragEvent event) {
      InventoryView view = event.getView();
      Inventory topInventory = view.getTopInventory();
      if (topInventory == null) {
         return false;
      } else {
         boolean result = false;
         int size = topInventory.getSize();

         for(Integer entry : event.getNewItems().keySet()) {
            if (entry < size) {
               result = true;
               break;
            }
         }

         return result;
      }
   }

   public static String serializeInventory(ItemStack[] source) {
      StringBuilder builder = new StringBuilder();

      for(ItemStack itemStack : source) {
         builder.append(serializeItemStack(itemStack));
         builder.append(";");
      }

      return builder.toString();
   }

   public static ItemStack[] deserializeInventory(String source) {
      List<ItemStack> items = new ArrayList();
      String[] split = source.split(";");

      for(String piece : split) {
         items.add(deserializeItemStack(piece));
      }

      return items.toArray(new ItemStack[items.size()]);
   }

   public static String serializeItemStack(ItemStack item) {
      StringBuilder builder = new StringBuilder();
      if (item == null) {
         return "null";
      } else {
         String isType = String.valueOf(item.getType().getId());
         builder.append("t@").append(isType);
         if (item.getDurability() != 0) {
            String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
         }

         if (item.getAmount() != 1) {
            String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
         }

         Map<Enchantment, Integer> isEnch = item.getEnchantments();
         if (isEnch.size() > 0) {
            for(Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
               builder.append(":e@").append(ench.getKey().getKey().getKey()).append("@").append(ench.getValue());
            }
         }

         if (item.hasItemMeta()) {
            ItemMeta imeta = item.getItemMeta();
            if (imeta.hasDisplayName()) {
               builder.append(":dn@").append(imeta.getDisplayName());
            }

            if (imeta.hasLore()) {
               builder.append(":l@").append(imeta.getLore());
            }
         }

         return builder.toString();
      }
   }

   public static ItemStack deserializeItemStack(String in) {
      ItemStack item = null;
      ItemMeta meta = null;
      if (in.equals("null")) {
         return new ItemStack(Material.AIR);
      } else {
         String[] split = in.split(":");

         for(String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String s2 = itemAttribute[0];
            switch(s2) {
               case "t":
                  item = new ItemStack(Material.getMaterial(String.valueOf(Integer.valueOf(itemAttribute[1]))));
                  meta = item.getItemMeta();
                  break;
               case "d":
                  if (item != null) {
                     item.setDurability(Short.valueOf(itemAttribute[1]));
                  }
                  break;
               case "a":
                  if (item != null) {
                     item.setAmount(Integer.valueOf(itemAttribute[1]));
                  }
                  break;
               case "e":
                  if (item != null) {
                     item.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(String.valueOf(itemAttribute[1]))), Integer.valueOf(itemAttribute[2]));
                  }
                  break;
               case "dn":
                  if (meta != null) {
                     meta.setDisplayName(itemAttribute[1]);
                  }
                  break;
               case "l":
                  itemAttribute[1] = itemAttribute[1].replace("[", "");
                  itemAttribute[1] = itemAttribute[1].replace("]", "");
                  List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                  for(int x = 0; x < lore.size(); ++x) {
                     String s = lore.get(x);
                     if (s != null && s.toCharArray().length != 0) {
                        if (s.charAt(0) == ' ') {
                           s = s.replaceFirst(" ", "");
                        }

                        lore.set(x, s);
                     }
                  }

                  if (meta != null) {
                     meta.setLore(lore);
                  }
            }
         }

         if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
         }

         return item;
      }
   }
}
