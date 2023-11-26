package org.shonivergames.secretlife.config_readers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.shonivergames.secretlife.Main;

import java.util.List;
import java.util.Random;

public class LootTableReader {
    private static final String configName = ".loot_tables.";
    private static final String subListIndicator = "<sub>";
    private static Random rnd;

    public static ItemStack getRandomItem(String configTitle, String configVar){
        if (rnd == null)
            rnd = new Random();

        String configPath = configTitle + configName + configVar;
        Material mat = getRandomMaterial(configPath);

        ItemStack item = new ItemStack(mat);
        if(mat == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
            Enchantment enchantment = getRandomEnchant(configPath);
            meta.addStoredEnchant(enchantment, getRandomEnchantLevel(enchantment), false);
            item.setItemMeta(meta);
        }
        else if(mat == Material.SPLASH_POTION){
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            PotionType potion = getRandomPotion(configPath);
            meta.setBasePotionType(potion);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static Material getRandomMaterial(String configPath){
        List<String> allOptions = Main.configFile.getStringList(configPath + ".loot");

        String draw = allOptions.get(rnd.nextInt(allOptions.size()));
        if(draw.contains(subListIndicator)){
            draw = draw.replace(subListIndicator, "");
            allOptions = Main.configFile.getStringList(configPath + "." + draw);
            draw = allOptions.get(rnd.nextInt(allOptions.size()));
        }

        return Material.getMaterial(draw);
    }

    private static Enchantment getRandomEnchant(String configPath){
        if (rnd == null)
            rnd = new Random();

        List<String> allOptions = Main.configFile.getStringList(configPath + ".enchanted_book");
        String draw = allOptions.get(rnd.nextInt(allOptions.size()));

        return Enchantment.getByKey(NamespacedKey.fromString(draw));
    }

    private static int getRandomEnchantLevel(Enchantment enchantment){
        if (rnd == null)
            rnd = new Random();

        return rnd.nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);
    }

    private static PotionType getRandomPotion(String configPath){
        if (rnd == null)
            rnd = new Random();

        List<String> allOptions = Main.configFile.getStringList(configPath + ".potion");
        String draw = allOptions.get(rnd.nextInt(allOptions.size()));

        return PotionType.valueOf(draw);
    }
}
