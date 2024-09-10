package org.shonivergames.secretlife;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

// In hindsight, this is written pretty poorly.
// Every manager should have handled its own get/set functions, they should NOT be contained here -
// but it's too late to change that now - too much work and too many things might break.
// The entire plugin is pretty poorly written, ngl.
// Wish I could've remade it completely, but I don't have the time or motivation for that anymore, sadly.
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

    public boolean isPlayerRegistered(Player player){
        String currentUUID = String.valueOf(player.getUniqueId());
        return dataConfig.contains(currentUUID);
    }
    public void registerPlayer(Player player){
        setValueAndSave(player, "name", player.getName());
        setEmptyPredeterminedTask(player);
    }

    public String getAndRemovePredeterminedTask(Player player){
        String task = getString(player, "predetermined_task").strip();
        setEmptyPredeterminedTask(player);
        if(task.isEmpty())
            return null;
        return task;
    }
    private void setEmptyPredeterminedTask(Player player){
        setPredeterminedTask(player, "");
    }
    public void setPredeterminedTask(Player player, String task){
        setValueAndSave(player, "predetermined_task", task);
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

    public void addToTaskHistory(Player player, String taskContent){
        List<String> taskHistory = getTaskHistory(player);
        taskHistory.add(taskContent);
        setValueAndSave(player, "task_history", taskHistory);
    }
    public List<String> getTaskHistory(Player player){
        return getStringList(player, "task_history");
    }
    public List<String> getAllPreviousTasks(){
        List<String> allTasks = new ArrayList<>();

        Set<String> allKeys = dataConfig.getKeys(false);
        for (String key : allKeys)
            allTasks.addAll(getStringList(key, "task_history"));

        return allTasks;
    }

    public boolean isOnTaskCooldown(Player player){
        return getBool(player, "is_on_task_cooldown");
    }
    public void startTaskCooldown(Player player){
        setValueAndSave(player, "is_on_task_cooldown", true);
    }
    public void endTaskCooldown(Player player){
        setValueAndSave(player, "is_on_task_cooldown", false);
    }
    public void endTaskCooldown(String playerUUID){ // Useful for if the player has logged out, but we still want to end their cooldown
        setValueAndSave(playerUUID, "is_on_task_cooldown", false);
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

    private <T> void setValueAndSave(Player player, String varName, T value) {
        String currentUUID = String.valueOf(player.getUniqueId());
        setValueAndSave(currentUUID, varName, value);
    }
    private <T> void setValueAndSave(String playerUUID, String varName, T value) {
        dataConfig.set(playerUUID + "." + varName, value);
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
    private List<String> getStringList(Player player, String varName) {
        return getStringList(String.valueOf(player.getUniqueId()), varName);
    }
    private List<String> getStringList(String playerUUID, String varName) {
        return Util.safeGetStringListFromConfig(dataConfig, playerUUID + "." + varName);
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
