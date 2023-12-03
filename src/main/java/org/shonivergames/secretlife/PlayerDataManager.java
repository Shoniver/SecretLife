package org.shonivergames.secretlife;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.SettingReader;

import javax.tools.JavaFileManager;
import java.io.*;
import java.util.logging.Level;

public class PlayerDataManager {
    private static final String fileName = "PlayerData.yml";

    private FileConfiguration dataConfig;
    private File dataFile;

    public PlayerDataManager(){
        loadData();
    }

    public void loadData() {
        this.dataFile = new File(Main.instance.getDataFolder(), fileName);
        this.dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(this.dataFile);
        }
        catch (IOException e) {
            Main.logger.log(Level.SEVERE, "Could not save player data " + this.dataFile, e);
        }
    }

    public String getTaskTitle(Player player){
        return getString(player, "current_task.item");
    }
    public String getTaskDifficulty(Player player){
        return getString(player, "current_task.difficulty");
    }
    public boolean getIsRedTask(Player player){
        return getBool(player, "current_task.is_red");
    }
    public void setTask(Player player, String taskTitle, String taskDifficulty, boolean isRedTask){
        setValueAndSave(player, "current_task.item", taskTitle);
        setValueAndSave(player, "current_task.difficulty", taskDifficulty);
        setValueAndSave(player, "current_task.is_red", isRedTask);
    }
    public void resetTask(Player player){
        setTask(player, "", "", false);
    }
    public boolean hasTask(Player player){
        String currentTask = getTaskTitle(player);
        return currentTask != null && !currentTask.isEmpty();
    }

    public int getLivesCount(Player player){
        return getInt(player, "lives");
    }

    public void setLivesCount(Player player, int livesCount){
        setValueAndSave(player, "lives", livesCount);
    }

    public boolean getCanGift(Player player){
        return getBool(player, "can_gift");
    }

    public void setCanGift(Player player, boolean canGift){
        setValueAndSave(player, "can_gift", canGift);
    }

    public boolean isPlayerRegistered(Player player){
        String currentUUID = String.valueOf(player.getUniqueId());
        return !dataConfig.contains(currentUUID);
    }

    private <T> void setValueAndSave(Player player, String varName, T value) {
        String currentUUID = String.valueOf(player.getUniqueId());
        dataConfig.set(currentUUID + "." + varName, value);
        saveData();
    }

    private int getInt(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return dataConfig.getInt(currentUUID + "." + varName);
    }

    private String getString(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return dataConfig.getString(currentUUID + "." + varName);
    }

    private boolean getBool(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return dataConfig.getBoolean(currentUUID + "." + varName);
    }

    public void deleteAllData(){
        for (Player player : Main.server.getOnlinePlayers()) {
            player.kickPlayer("Deleting all of your saved SecretLife player data.");
        }
        dataFile.delete();
        loadData();
    }
}
