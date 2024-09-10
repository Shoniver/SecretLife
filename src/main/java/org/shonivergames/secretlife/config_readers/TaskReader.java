package org.shonivergames.secretlife.config_readers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.PlayerDataManager;
import org.shonivergames.secretlife.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskReader {
    private static final String configName = ".tasks.";
    private static Random rnd;

    public static ItemStack getRandomTask(String configTitle, Player player, String difficulty){
        String configPath = configTitle + configName;

        String taskTitleFormat = SettingReader.getString(configPath, "title_format");
        String taskTitle = Util.getFormattedString(taskTitleFormat, player.getName());

        String taskContent = Main.playerData.getAndRemovePredeterminedTask(player);
        if(taskContent == null)
            taskContent = getRandomContent(configPath, player, difficulty);

        Main.playerData.addToTaskHistory(player, taskContent);

        String taskContentFormat = SettingReader.getString(configPath, "content_format");
        // Fill in a random player
        taskContent = Util.getFormattedString(taskContent, Util.getRandomOtherPlayer(player).getName());
        // Format the content according to the "content_format" config value
        taskContent = Util.getFormattedString(taskContentFormat, difficulty.toUpperCase(), taskContent);

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(taskTitle);
        meta.setDisplayName(taskTitle);
        meta.setAuthor(SettingReader.getAdminName());
        Util.writeBookContent(meta, taskContent);
        book.setItemMeta(meta);

        return book;
    }
    public static boolean doesPlayerHaveAvailableTasks(String configTitle, Player player, String difficulty){
        return getRandomContent(configTitle + configName, player, difficulty) != null;
    }

    private static String getRandomContent(String configPath, Player player, String difficulty) {
        if (rnd == null)
            rnd = new Random();

        List<String> allTasks = new ArrayList<>();
        String pathAddition = difficulty;
        if(LivesManager.isRedPlayer(player)) {
            pathAddition = "red." + pathAddition;
        }
        else {
            if (LivesManager.isThereYellowPlayer() && !LivesManager.isYellowPlayer(player))
                allTasks.addAll(Util.safeGetStringListFromConfig(configPath + "has_yellows." + pathAddition));
            else // If there are no yellow players or THIS player is yellow
                allTasks.addAll(Util.safeGetStringListFromConfig(configPath + "no_yellows." + pathAddition));

            if (LivesManager.isThereRedPlayer() && !LivesManager.isRedPlayer(player))
                allTasks.addAll(Util.safeGetStringListFromConfig(configPath + "has_reds." + pathAddition));
            else
                allTasks.addAll(Util.safeGetStringListFromConfig(configPath + "no_reds." + pathAddition));
        }
        allTasks.addAll(Util.safeGetStringListFromConfig(configPath + pathAddition));

        if(!SettingReader.getBool(configPath, "can_tasks_appear_more_than_once." + pathAddition)){
            List<String> pastTasks = Main.playerData.getAllPreviousTasks();
            allTasks.removeAll(pastTasks);
        }
        else if(!SettingReader.getBool(configPath, "can_player_get_repeat_tasks." + pathAddition)){
            List<String> taskHistory = Main.playerData.getTaskHistory(player);
            allTasks.removeAll(taskHistory);
        }

        if(allTasks.isEmpty())
            return null;
        return allTasks.get(rnd.nextInt(allTasks.size()));
    }
}
