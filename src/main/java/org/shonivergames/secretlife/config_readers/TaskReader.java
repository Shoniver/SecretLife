package org.shonivergames.secretlife.config_readers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskReader {
    private static final String configName = ".tasks.";
    private static Random rnd;

    public static ItemStack getRandomTask(String configTitle, Player player, String difficulty){
        String configPath = configTitle + configName;

        String taskTitle = SettingReader.getString(configPath, "task_title");
        taskTitle = Util.getFormattedString(taskTitle, player.getName());

        String taskContent = SettingReader.getString(configPath, "task_content");
        taskContent = Util.getFormattedString(taskContent, difficulty.toUpperCase(), getRandomContent(configPath, player, difficulty));

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(taskTitle);
        meta.setDisplayName(taskTitle);
        meta.setAuthor(SettingReader.getAdminName());
        meta.addPage(taskContent);
        book.setItemMeta(meta);

        return book;
    }

    private static String getRandomContent(String configPath, Player player, String difficulty) {
        if (rnd == null)
            rnd = new Random();

        List<String> allTasks = new ArrayList<>();
        if(LivesManager.isRedPlayer(player))
            allTasks.addAll(Main.configFile.getStringList(configPath + ".red." + difficulty));
        else {
            allTasks.addAll(Main.configFile.getStringList(configPath + "." + difficulty));

            if (LivesManager.isThereYellowPlayer() && !LivesManager.isYellowPlayer(player))
                allTasks.addAll(Main.configFile.getStringList(configPath + ".has_yellows." + difficulty));
            else
                allTasks.addAll(Main.configFile.getStringList(configPath + ".no_yellows." + difficulty));

            if (LivesManager.isThereRedPlayer() && !LivesManager.isRedPlayer(player))
                allTasks.addAll(Main.configFile.getStringList(configPath + ".has_reds." + difficulty));
            else
                allTasks.addAll(Main.configFile.getStringList(configPath + ".no_reds." + difficulty));
        }

        String draw = allTasks.get(rnd.nextInt(allTasks.size()));
        return Util.getFormattedString(draw, Util.getRandomOtherPlayer(player).getName());
    }
}
