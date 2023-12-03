package org.shonivergames.secretlife;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.shonivergames.secretlife.config_readers.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TasksManager {
    private static final String baseConfigPath = "tasks_manager";

    public static void init(){
        manageHasTaskEffect();
    }

    public static void initPlayer(Player player){
        // Just in case it's turned on, for whatever reason -
        // COULD technically happen if the server crashes at EXACTLY the wrong moment
        Main.playerData.endTaskCooldown(player);
    }

    public static void giveTaskAnimated(Player player, boolean isHardTask){
        if(isHardTask){
            concludeTask(player, "reroll_task");
        }

        // If this player gets constant tasks AND currently has a task, don't continue forward.
        if(shouldGetConstantTasks(player) && Main.playerData.hasTask(player))
            return;

        new BukkitRunnable() {
            boolean init = false;
            int i;
            int amount;
            String playerUUID;

            @Override
            public void run() {
                if(!init){
                    init = true;

                    i = 1;
                    amount = SettingReader.getInt(baseConfigPath, "amount_of_messages_on_obtain." + convertToDifficulty(isHardTask));

                    playerUUID = String.valueOf(player.getUniqueId());
                    Main.playerData.startTaskCooldown(player);
                }

                String currentVar = "task_obtain." + convertToDifficulty(isHardTask) + ".";

                if(i <= amount){
                    TitleReader.send(baseConfigPath, currentVar + i, player);
                    if(i == 1)
                        SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "start", player, false);
                    else
                        SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "tick", player, false);
                }
                else if (i == amount + 1) {
                    if(Util.isPlayerOnline(player))
                        player.playEffect(EntityEffect.TOTEM_RESURRECT);
                }
                else {
                    if(Util.isPlayerOnline(player)) {
                        Util.spawnItemForPlayer(player, player.getLocation(), getNewPlayerTask(player, isHardTask));
                        SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "obtained", player, true);
                    }

                    // Sends the UUID instead of the player variable,
                    // that way it works even if the player has logged out
                    Main.playerData.endTaskCooldown(playerUUID);
                    cancel();
                }

                i++;
            }
        }.runTaskTimer(Main.instance, 0L, SettingReader.getInt(baseConfigPath, "delay_between_obtain_messages." + convertToDifficulty(isHardTask)));
    }

    private static ItemStack getNewPlayerTask(Player player, boolean isHardTask) {
        String difficulty = convertToDifficulty(isHardTask);
        ItemStack taskItem = TaskReader.getRandomTask(baseConfigPath, player, difficulty);

        Main.playerData.setTask(player, taskItem.getItemMeta().getDisplayName(), difficulty, LivesManager.isRedPlayer(player));

        return taskItem;
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void passTask(Player player) {
        // Get the current task's settings before removing the task
        String difficulty = Main.playerData.getTaskDifficulty(player);
        String path = "";
        if(Main.playerData.getIsRedTask(player))
            path += "red.";
        path += difficulty;

        concludeTask(player, "pass_task");

        // Give rewards & loot
        int healthReward = SettingReader.getInt(baseConfigPath, "reward." + path);
        int healthChange = HealthManager.addHealth(player, healthReward, false);
        createTaskRewardLoot(player, healthReward - healthChange, path);

        if(shouldGetConstantTasks(player))
            spawnItemAtLootPool(player, getNewPlayerTask(player, false));
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void failTask(Player player) {
        // Get the current task's settings before removing the task
        String difficulty = Main.playerData.getTaskDifficulty(player);
        String configVar = "penalty.";
        if(Main.playerData.getIsRedTask(player))
            configVar += "red.";
        configVar += difficulty;

        concludeTask(player, "fail_task");

        HealthManager.removeHealth(player, SettingReader.getInt(baseConfigPath, configVar), SettingReader.getBool(baseConfigPath, "can_penalty_kill"));

        if(shouldGetConstantTasks(player))
            spawnItemAtLootPool(player, getNewPlayerTask(player, false));
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    private static void concludeTask(Player player, String actionType) {
        // Get the current task details
        ItemStack taskItem = getTaskFromInventory(player);
        String taskDifficulty = Main.playerData.getTaskDifficulty(player);
        Boolean isRedTask = Main.playerData.getIsRedTask(player);
        String taskContent = Util.extractBookContent((BookMeta)taskItem.getItemMeta());

        // Remove the task from the player's inventory
        Inventory inv = player.getInventory();
        inv.remove(taskItem);

        Main.playerData.resetTask(player);
        MessageReader.sendPublic(baseConfigPath, actionType, player.getName(), taskContent);

        handleTasksLog(player.getName(), taskDifficulty, String.valueOf(isRedTask), actionType, taskContent);
    }

    public static String getRerollTaskError(Player player) {
        // If red players aren't allowed to reroll tasks
        if(!SettingReader.getBool(baseConfigPath, "can_reds_get_hard_tasks") && LivesManager.isRedPlayer(player))
            return "red_cant_reroll";

        // Can't reroll without a task
        String removeTaskError = getRemoveTaskError(player);
        if(removeTaskError != null)
            return removeTaskError;

        // if the player has requested a hard task, but already HAS a hard task, invalid request.
        if (checkIsHardTask(Main.playerData.getTaskDifficulty(player)))
            return "already_has_hard";
        // if the player is currently in the middle of getting a task
        if (Main.playerData.isOnTaskCooldown(player))
            return "on_task_cooldown";
        // If the player physically doesn't have any tasks to pick from.
        // This can happen if the config file is set to not allow repeat tasks, for example.
        if(!TaskReader.doesPlayerHaveAvailableTasks(baseConfigPath, player, "hard"))
            return "no_tasks_available";

        return null;
    }
    public static String getPassTaskError(Player player){
        return getRemoveTaskError(player);
    }
    public static String getFailTaskError(Player player){
        return getRemoveTaskError(player);
    }
    public static String getSessionBeginError(Player player) {
        // If the player already has a task, can't start the session!
        // This does NOT apply if the player has constant tasks.
        if(!shouldGetConstantTasks(player) && Main.playerData.hasTask(player))
            return "has_incomplete_task";
        // if the player is currently in the middle of getting a task
        if (Main.playerData.isOnTaskCooldown(player))
            return "on_task_cooldown";
        // If the player physically doesn't have any tasks to pick from.
        // This can happen if the config file is set to not allow repeat tasks, for example.
        if(!TaskReader.doesPlayerHaveAvailableTasks(baseConfigPath, player, "normal"))
            return "no_tasks_available";

        return null;
    }
    private static String getRemoveTaskError(Player player) {
        // If the player doesn't have a task, it can not be removed
        if(!Main.playerData.hasTask(player))
            return "has_no_task";
        // If the player has a task, but doesn't have it in his inventory, can't give new task.
        if (Main.playerData.hasTask(player) && !isTaskInPlayerInv(player))
            return "no_task_in_inv";

        return null;
    }

    private static boolean isTaskInPlayerInv(Player player){
        return getTaskFromInventory(player) != null;
    }
    private static ItemStack getTaskFromInventory(Player player) {
        String taskItemName = Main.playerData.getTaskTitle(player);
        Inventory inv = player.getInventory();
        ItemStack[] allItems = inv.getContents();
        for (ItemStack item : allItems) {
            if (item != null &&
                    item.getType() == Material.WRITTEN_BOOK &&
                    ((BookMeta) item.getItemMeta()).getTitle().equals(taskItemName)) {

                return item;
            }
        }

        return null;
    }
    private static boolean shouldGetConstantTasks(Player player){
        return LivesManager.isRedPlayer(player) && SettingReader.getBool(baseConfigPath, "constant_red_tasks");
    }

    public static void manageHasTaskEffect(){
        // Spawns a particle effect for as long as the player has this task
        new BukkitRunnable() {
            boolean init = false;
            int yOffset;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    yOffset = SettingReader.getInt(baseConfigPath, "has_task_effect.offset");
                }

                if(!SettingReader.getBool(baseConfigPath, "has_task_effect.enabled"))
                    return;

                // For every player with a task, spawn an effect
                for (Player player : Main.server.getOnlinePlayers()) {
                    if(!Main.playerData.hasTask(player))
                        continue;

                    Location location = player.getLocation().clone();
                    location.setY(location.getY() + yOffset);
                    VisualEffectReader.play(baseConfigPath, "has_task." + Main.playerData.getTaskDifficulty(player), player, location, false, true);
                }
            }
        }.runTaskTimer(Main.instance, 0L, SettingReader.getInt(baseConfigPath, "has_task_effect.delay"));
    }

    private static String convertToDifficulty(boolean isHardTask){
        if (isHardTask)
            return "hard";
        return "normal";
    }
    private static boolean checkIsHardTask(String difficulty){
        return difficulty.equals("hard");
    }

    public static void createTaskRewardLoot(Player player, int healthToConvert, String lootTable){
        new BukkitRunnable() {
            boolean init = false;
            double itemsCount;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    double lootPerHealth = SettingReader.getDouble(baseConfigPath, "loot.loot_per_heath");
                    itemsCount = lootPerHealth * healthToConvert;
                    return; // Skips the first loop, just to give a tiny delay
                }

                if(itemsCount <= 0) {
                    cancel();
                    return;
                }

                spawnItemAtLootPool(player, LootTableReader.getRandomItem(baseConfigPath, lootTable));
                itemsCount--;
            }
        }.runTaskTimer(Main.instance, 0L, SettingReader.getInt(baseConfigPath, "loot.delay_between_items"));
    }

    public static void spawnItemAtLootPool(Player player, ItemStack item) {
        Location spawnLocation = LocationReader.getRandomLocation(baseConfigPath, "loot_spawn");
        Util.spawnItemForPlayer(player, spawnLocation, item);
        SoundEffectReader.playAtLocation(baseConfigPath, "loot_spawn", spawnLocation);
    }

    public static void handleStartOfSession(){
        handleTasksLog("START OF SESSION", "----", "----", "----", "----");
    }

    private static void handleTasksLog(String playerName, String taskDifficulty, String isRedTask, String actionType, String taskContent){
        if(SettingReader.getBool(baseConfigPath, "tasks_log.enabled")){
            String link = SettingReader.getString(baseConfigPath, "tasks_log.link");
            playerName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
            taskDifficulty = URLEncoder.encode(taskDifficulty, StandardCharsets.UTF_8);
            isRedTask = URLEncoder.encode(isRedTask, StandardCharsets.UTF_8);
            actionType = URLEncoder.encode(actionType, StandardCharsets.UTF_8);
            taskContent = URLEncoder.encode(taskContent, StandardCharsets.UTF_8);
            link = Util.getFormattedString(link, playerName, taskDifficulty, isRedTask, actionType, taskContent);
            Util.openLink(link);
        }
    }
}
