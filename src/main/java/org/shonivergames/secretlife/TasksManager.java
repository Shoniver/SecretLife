package org.shonivergames.secretlife;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.shonivergames.secretlife.config_readers.*;

public class TasksManager {
    private static final String baseConfigPath = "tasks_manager";

    public static void giveTaskAnimated(Player player, boolean isHardTask){
        if(isHardTask){
            ItemStack taskItem = removeTask(player);
            String task = extractTaskContent(taskItem);
            MessageReader.sendPublic(baseConfigPath, "reroll_task", player.getName(), task);
        }

        new BukkitRunnable() {
            boolean init = false;
            int i;
            int amount;
            String playerName;

            @Override
            public void run() {
                if(!init){
                    init = true;

                    i = 1;
                    amount = SettingReader.getInt(baseConfigPath, "amount_of_messages_on_obtain." + convertToDifficulty(isHardTask));

                    playerName = player.getName();

                }

                Player player = Util.getPlayerFromName(playerName); // TODO: Is this really needed?
                String currentVar = "task_obtain." + convertToDifficulty(isHardTask) + ".";

                if(i <= amount){
                    TitleReader.send(baseConfigPath, currentVar + i, player);
                    if(i == 1)
                        SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "start", player, false);
                    else
                        SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "tick", player, false);
                }
                else if (i == amount + 1) {
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                }
                else {
                    TasksManager.giveTask(player, isHardTask);
                    SoundEffectReader.playAtPlayer(baseConfigPath, currentVar + "obtained", player, true);
                    cancel();
                }

                i++;
            }
        }.runTaskTimer(Main.instance, 0L, SettingReader.getInt(baseConfigPath, "delay_between_obtain_messages." + convertToDifficulty(isHardTask)));
    }

    public static void giveTask(Player player, boolean isHardTask) {
        String difficulty = convertToDifficulty(isHardTask);
        ItemStack taskItem = TaskReader.getRandomTask(baseConfigPath, player, difficulty);

        Util.spawnItemForPlayer(player, player.getLocation(), taskItem);

        Main.playerData.setTask(player, taskItem.getItemMeta().getDisplayName(), difficulty);
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void passTask(Player player) {
        // Get the difficulty before removing the task
        String difficulty = Main.playerData.getTaskDifficulty(player);

        ItemStack taskItem = removeTask(player);
        String task = extractTaskContent(taskItem);
        MessageReader.sendPublic(baseConfigPath, "pass_task", player.getName(), task);

        // Give rewards & loot
        String path = "";
        if(LivesManager.isRedPlayer(player))
            path += "red.";
        path += difficulty;
        int healthReward = SettingReader.getInt(baseConfigPath, "reward." + path);
        int healthChange = HealthManager.addHealth(player, healthReward, false);
        createTaskRewardLoot(player, healthReward - healthChange, path);
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void failTask(Player player) {
        // Get the difficulty before removing the task
        String difficulty = Main.playerData.getTaskDifficulty(player);

        ItemStack taskItem = removeTask(player);
        String task = extractTaskContent(taskItem);
        MessageReader.sendPublic(baseConfigPath, "fail_task", player.getName(), task);

        String configVar = "penalty.";
        if(LivesManager.isRedPlayer(player))
            configVar += "red.";
        HealthManager.removeHealth(player, SettingReader.getInt(baseConfigPath, configVar + difficulty), SettingReader.getBool(baseConfigPath, "can_penalty_kill"));
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    private static ItemStack removeTask(Player player) {
        if(!Main.playerData.hasTask(player))
            return null;

        ItemStack taskItem = getTaskInInventory(player);
        Inventory inv = player.getInventory();
        inv.remove(taskItem);

        Main.playerData.resetTask(player);
        return taskItem;
    }

    public static String getRerollTaskError(Player player) {
        // Can't reroll without a task
        String removeTaskError = getRemoveTaskError(player);
        if(removeTaskError != null)
            return removeTaskError;

        // If red players aren't allowed to reroll tasks
        if(!SettingReader.getBool(baseConfigPath, "can_reds_get_hard_tasks") && LivesManager.isRedPlayer(player))
            return "red_cant_reroll";
        // if the player has requested a hard task, but already HAS a hard task, invalid request.
        else if (Main.playerData.getTaskDifficulty(player).equals("hard"))
            return "already_has_hard";

        return null;
    }
    public static String getRemoveTaskError(Player player) {
        // If the player doesn't have a task, it can not be removed
        if(!Main.playerData.hasTask(player))
            return "has_no_task";
        // If the player has a task, but doesn't have it in his inventory, can't give new task.
        if (Main.playerData.hasTask(player) && !isTaskInPlayerInv(player))
            return "no_task_in_inv";

        return null;
    }
    public static String getReceiveNewTaskError(Player player) {
        // If the player has a task, but doesn't have it in his inventory, can't give new task.
        if (Main.playerData.hasTask(player))
            return "has_incomplete_task";

        return null;
    }

    private static boolean isTaskInPlayerInv(Player player){
        return getTaskInInventory(player) != null;
    }
    private static ItemStack getTaskInInventory(Player player) {
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
    private static String extractTaskContent(ItemStack taskItem){
        return ((BookMeta) taskItem.getItemMeta()).getPage(1);
    }

    public static void createTaskRewardLoot(Player player, int healthToConvert, String lootTable){
        new BukkitRunnable() {
            boolean init = false;
            double itemsCount;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    double lootPerHealth = SettingReader.getDouble(baseConfigPath, "loot_per_heath");
                    itemsCount = lootPerHealth * healthToConvert;
                }

                if(itemsCount <= 0) {
                    cancel();
                    return;
                }

                ItemStack itemStack = LootTableReader.getRandomItem(baseConfigPath, lootTable);
                Location spawnLocation = LocationReader.getRandomLocation(baseConfigPath, "loot_spawn");

                Util.spawnItemForPlayer(player, spawnLocation, itemStack);
                SoundEffectReader.playAtLocation(baseConfigPath, "loot_spawn", player, spawnLocation, true);

                itemsCount--;
            }
        }.runTaskTimer(Main.instance, 0L, SettingReader.getInt(baseConfigPath, "delay_between_items"));
    }
}
