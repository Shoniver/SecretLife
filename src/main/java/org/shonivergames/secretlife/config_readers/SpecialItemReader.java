package org.shonivergames.secretlife.config_readers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpecialItemReader {
    private static final String configName = ".special_items.";

    public static ItemStack get(String configTitle, String configVar){
        String configPath = configTitle + configName + configVar;

        ItemStack item = new ItemStack(Material.getMaterial(Main.configFile.getString(configPath + ".type")));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Main.configFile.getString(configPath + ".name"));
        meta.setLore(Util.safeGetStringListFromConfig(configPath + ".lore"));
        item.setItemMeta(meta);

        return item;
    }

    public static List<ItemStack> getAll(String configTitle, String configSection){
        List<ItemStack> allItems = new ArrayList<>();

        Set<String> allSubKeys = Main.configFile.getConfigurationSection(configTitle + configName + configSection).getKeys(false);
        for (String key : allSubKeys)
            allItems.add(get(configTitle, configSection + "." + key));

        return allItems;
    }
}
