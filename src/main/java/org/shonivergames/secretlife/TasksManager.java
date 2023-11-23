package org.shonivergames.secretlife;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TasksManager {
    private static Random rnd;

    public static void giveAnimatedTask(Player player, boolean isHardTask){
        String configPath = "tasks.start";

        if(isHardTask){
            configPath += "_hard";

            ItemStack taskItem = removeTask(player);
            if(taskItem == null)
                return;
            String task = ((BookMeta) taskItem.getItemMeta()).getPage(1);
            TextManager.sendFormattedPublicMessage("messages.commands.tasks.public.rerolled_task", player, task);
        }

        // Spawns a particle effect for as long as the player has this task
        new BukkitRunnable() {
            boolean init = false;
            String[] texts;
            int i;
            int delay;
            int fade;
            String playerName;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    texts = new String[4];
                    String configPath = "tasks.start";
                    if(isHardTask)
                        configPath += "_hard";
                    for(int j = 0; j < texts.length; j++)
                        texts[j] = Main.configFile.getString(configPath + ".text" + (j+1));
                    i = 0;

                    delay = Main.configFile.getInt(configPath + ".delay");
                    fade = Main.configFile.getInt(configPath + ".fade");
                    playerName = player.getName();
                }

                Player player = PlayersManager.getPlayerFromName(playerName);

                if(i == 0) {
                    TextManager.sendFormattedTitle(player, texts[i], "", fade, 200, 0, null, null);
                    String configPath = "task.obtain";
                    if(isHardTask)
                        configPath += "_hard";
                    SoundManager.playSoundEffect(player, player.getLocation(), configPath, false);
                }
                else if(i < texts.length - 1) {
                    TextManager.sendFormattedTitle(player, texts[i], "", 0, 200, 0, null, null);
                    SoundManager.playSoundEffect(player, player.getLocation(), "tick", false);
                }
                else if(i == texts.length - 1) {
                    TextManager.sendFormattedTitle(player, texts[i], "", 0, delay, fade, null, null);
                    SoundManager.playSoundEffect(player, player.getLocation(), "tick", false);
                }
                else if (i == texts.length) {
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                }
                else {
                    TasksManager.giveTask(player, isHardTask);
                    cancel();
                }

                i++;
            }
        }.runTaskTimer(Main.instance, 0L, Main.configFile.getInt(configPath + ".delay"));
    }

    private static void giveTask(Player player, boolean isHardTask) {
        if(isHardTask)
            SoundManager.playSoundEffect(player, player.getLocation(), "task.reroll", true);

        String difficulty = "normal";
        if (isHardTask)
            difficulty = "hard";

        String taskTitle = Main.configFile.getString("tasks.title_color") + player.getName() + "'s Task";
        String taskContent = TextManager.getFormattedText(getRandomTask(player, difficulty), PlayersManager.getRandomOtherPlayer(player), null);
        taskContent = difficulty.toUpperCase() + " TASK: " + taskContent;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(taskTitle);
        meta.setDisplayName(taskTitle);
        meta.setAuthor(Main.configFile.getString("tasks.author"));
        meta.addPage(taskContent);
        book.setItemMeta(meta);
        player.getInventory().addItem(book);

        PlayersManager.setPlayerValue(player, "current_task.item", taskTitle);
        PlayersManager.setPlayerValue(player, "current_task.difficulty", difficulty);
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void passTask(Player player) {
        // Get the difficulty before removing the task
        String difficulty = getCurrentTaskDifficulty(player);

        ItemStack taskItem = removeTask(player);
        if(taskItem == null)
            return;
        String task = ((BookMeta) taskItem.getItemMeta()).getPage(1);
        TextManager.sendFormattedPublicMessage("messages.commands.tasks.public.pass_task", player, task);
        SoundManager.playSoundEffect(player, player.getLocation(), "task.pass", true);

        // Give loot
        int healthReward = Main.configFile.getInt("tasks.reward." + difficulty + ".health");
        int healthChange = HealthManager.addHealth(player, healthReward, Main.configFile.getString("settings.admin_name"), false);
        convertHealthToLoot(player, healthReward - healthChange, difficulty);
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    public static void failTask(Player player) {
        // Get the difficulty before removing the task
        String difficulty = getCurrentTaskDifficulty(player);

        ItemStack taskItem = removeTask(player);
        if(taskItem == null)
            return;
        String task = ((BookMeta) taskItem.getItemMeta()).getPage(1);
        TextManager.sendFormattedPublicMessage("messages.commands.tasks.public.fail_task", player, task);
        SoundManager.playSoundEffect(player, player.getLocation(), "task.fail", true);

        HealthManager.removeHealth(player, Main.configFile.getInt("tasks.penalty." + difficulty), Main.configFile.getString("settings.admin_name"));
    }

    private static String getCurrentTaskDifficulty(Player player){
        return PlayersManager.getPlayerString(player, "current_task.difficulty");
    }

    // Assumes the task exists in the player inventory. Must be checked before this!
    private static ItemStack removeTask(Player player) {
        if(!doesPlayerHaveIncompleteTask(player))
            return null;

        ItemStack taskItem = getTaskInInventory(player);
        Inventory inv = player.getInventory();
        inv.remove(taskItem);

        resetPlayerTask(player);
        return taskItem;
    }

    public static void resetPlayerTask(Player player){
        PlayersManager.setPlayerValue(player, "current_task.item", "");
        PlayersManager.setPlayerValue(player, "current_task.difficulty", "");
    }

    private static String getRandomTask(Player player, String difficulty) {
        if (rnd == null)
            rnd = new Random();

        List<String> allTasks = new ArrayList<>();
        String taskTypePath = "tasks.";
        if(PlayersManager.isRedPlayer(player))
            allTasks.addAll(Main.configFile.getStringList(taskTypePath + ".red"));
        else {
            allTasks.addAll(Main.configFile.getStringList(taskTypePath + "." + difficulty));
            if (PlayersManager.isThereYellowPlayer() && !PlayersManager.isYellowPlayer(player))
                allTasks.addAll(Main.configFile.getStringList(taskTypePath + ".has_yellows." + difficulty));
            else
                allTasks.addAll(Main.configFile.getStringList(taskTypePath + ".no_yellows." + difficulty));

            if (PlayersManager.isThereRedPlayer() && !PlayersManager.isRedPlayer(player))
                allTasks.addAll(Main.configFile.getStringList(taskTypePath + ".has_reds." + difficulty));
            else
                allTasks.addAll(Main.configFile.getStringList(taskTypePath + ".no_reds." + difficulty));
        }

        return allTasks.get(rnd.nextInt(allTasks.size()));
    }

    public static boolean canRerollTask(CommandSender sender, Player player) {
        // Can't reroll without a task
        if(!canRemoveTask(sender, player))
            return false;

        boolean commandFailed = false;
        String commandFailedMessagePath = "";

        if(PlayersManager.isRedPlayer(player)){
            commandFailedMessagePath = "red_cant_reroll";
            commandFailed = true;
        }
        // if the player has requested a hard task, but had no normal task, invalid request.
        else if (!doesPlayerHaveIncompleteTask(player)) {
            commandFailedMessagePath = "reroll_without_normal";
            commandFailed = true;
        }
        // if the player has requested a hard task, but already HAS a hard task, invalid request.
        else if (getCurrentTaskDifficulty(player).equals("hard")) {
            commandFailedMessagePath = "already_has_hard";
            commandFailed = true;
        }

        if (commandFailed) {
            TextManager.sendFormattedPrivateMessage(player, "messages.commands.tasks." + commandFailedMessagePath, player, null);
            if(sender != null)
                TextManager.sendFormattedPrivateMessage(sender, "messages.commands.tasks.server." + commandFailedMessagePath, player, null);
            return false;
        }
        else
            return true;
    }

    public static boolean canRemoveTask(CommandSender sender, Player player) {
        boolean commandFailed = false;
        String commandFailedMessagePath = "";

        // If the player has a task, but doesn't have it in his inventory, can't give new task.
        if (doesPlayerHaveIncompleteTask(player) && !isTaskInPlayerInv(player)) {
            commandFailedMessagePath = "no_task_in_inv";
            commandFailed = true;
        }

        if (commandFailed) {
            TextManager.sendFormattedPrivateMessage(player, "messages.commands.tasks." + commandFailedMessagePath, player, null);
            if(sender != null)
                TextManager.sendFormattedPrivateMessage(sender, "messages.commands.tasks.server." + commandFailedMessagePath, player, null);
            return false;
        }
        else
            return true;
    }

    public static boolean canPlayerInteract(CommandSender sender, Player player){
        boolean commandFailed = false;
        String commandFailedMessagePath = "";

        // If the player doesn't have a task, they can not interact with anything
        if(!doesPlayerHaveIncompleteTask(player)){
            commandFailedMessagePath = "has_no_task";
            commandFailed = true;
        }

        if (commandFailed) {
            TextManager.sendFormattedPrivateMessage(player, "messages.commands.tasks." + commandFailedMessagePath, player, null);
            if(sender != null)
                TextManager.sendFormattedPrivateMessage(sender, "messages.commands.tasks.server." + commandFailedMessagePath, player, null);
            return false;
        }
        else
            return true;
    }

    public static boolean canGetNewTask(CommandSender sender, Player player) {
        boolean commandFailed = false;
        String commandFailedMessagePath = "";

        // If the player has a task, but doesn't have it in his inventory, can't give new task.
        if (doesPlayerHaveIncompleteTask(player)) {
            commandFailedMessagePath = "has_incomplete_task";
            commandFailed = true;
        }

        if (commandFailed) {
            TextManager.sendFormattedPrivateMessage(player, "messages.commands.tasks." + commandFailedMessagePath, player, null);
            if(sender != null)
                TextManager.sendFormattedPrivateMessage(sender, "messages.commands.tasks.server." + commandFailedMessagePath, player, null);
            return false;
        }
        else
            return true;
    }

    private static boolean doesPlayerHaveIncompleteTask(Player player) {
        String currentTask = PlayersManager.getPlayerString(player, "current_task.item");
        return currentTask != null && !currentTask.isEmpty();
    }

    private static boolean isTaskInPlayerInv(Player player){
        return getTaskInInventory(player) != null;
    }

    private static ItemStack getTaskInInventory(Player player) {
        String taskItemName = PlayersManager.getPlayerString(player, "current_task.item");
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

    private static void convertHealthToLoot(Player player, int health, String difficulty){
        new BukkitRunnable() {
            boolean init = false;
            double itemsCount;
            Location spawnLocation;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    double lootPerHealth = Main.configFile.getDouble("tasks.reward.settings.loot_per_heath");
                    itemsCount = lootPerHealth * health;
                    spawnLocation = Util.getConfigLocation("tasks.reward.settings.spawn");
                }
                if(itemsCount <= 0) {
                    cancel();
                    return;
                }

                Material mat = getRandomLoot(difficulty);
                ItemStack item = new ItemStack(mat);
                if(mat == Material.ENCHANTED_BOOK){
                    Enchantment enchantment = getRandomEnchant(difficulty);
                    item.addUnsafeEnchantment(enchantment, getRandomEnchantLevel(enchantment));
                }
                else if(mat == Material.SPLASH_POTION){
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    PotionType potion = getRandomPotion(difficulty);
                    meta.setBasePotionType(potion);
                    item.setItemMeta(meta);
                }
                spawnLocation.getWorld().dropItemNaturally(spawnLocation, item);
                SoundManager.playSoundEffect(player, spawnLocation, "task.loot_drop", true);

                itemsCount--;
            }
        }.runTaskTimer(Main.instance, 0L, Main.configFile.getInt("tasks.reward.settings.delay_between_items"));
    }

    private static Material getRandomLoot(String difficulty){
        if (rnd == null)
            rnd = new Random();

        String configPath = "tasks.reward";

        List<String> allOptions = Main.configFile.getStringList(configPath + ".normal.loot");
        if(difficulty.equals("hard"))
            allOptions.addAll(Main.configFile.getStringList(configPath + ".hard.loot"));

        String draw = allOptions.get(rnd.nextInt(allOptions.size()));
        String subListIndicator = Main.configFile.getString(configPath + ".settings.sub_category_indicator");

        if(draw.contains(subListIndicator)){
            draw = draw.replace(subListIndicator, "");
            configPath += "." + difficulty + "." + draw;
            allOptions = Main.configFile.getStringList(configPath);
            draw = allOptions.get(rnd.nextInt(allOptions.size()));
        }

        return Material.getMaterial(draw);
    }

    private static Enchantment getRandomEnchant(String difficulty){
        if (rnd == null)
            rnd = new Random();

        List<String> allOptions = Main.configFile.getStringList("tasks.reward." + difficulty + ".enchanted_book");
        String draw = allOptions.get(rnd.nextInt(allOptions.size()));

        return Enchantment.getByKey(NamespacedKey.fromString(draw));
    }

    private static int getRandomEnchantLevel(Enchantment enchantment){
        if (rnd == null)
            rnd = new Random();

        return rnd.nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);
    }

    private static PotionType getRandomPotion(String difficulty){
        if (rnd == null)
            rnd = new Random();

        List<String> allOptions = Main.configFile.getStringList("tasks.reward." + difficulty + ".potion");
        String draw = allOptions.get(rnd.nextInt(allOptions.size()));

        return PotionType.valueOf(draw);
    }

    public static void manageHasTaskEffect(){
        // Spawns a particle effect for as long as the player has this task
        new BukkitRunnable() {
            boolean init = false;
            int yOffset;
            int intensity;

            @Override
            public void run() {
                if(!init){
                    init = true;
                    yOffset = Main.configFile.getInt("effects.player_with_unfinished_task.height_above_player");
                    intensity = Main.configFile.getInt("intensity");
                }

                // For every player with a task, spawn an effect
                for (Player player : Main.server.getOnlinePlayers()) {
                    if(!doesPlayerHaveIncompleteTask(player))
                        continue;

                    Location location = player.getLocation().clone();
                    location.setY(location.getY() + yOffset);
                    String difficulty = getCurrentTaskDifficulty(player);
                    Particle particleType = Particle.valueOf(Main.configFile.getString("effects.player_with_unfinished_task." + difficulty + "_effect"));

                    // Spawns an effect for all players except the player with the task
                    for (Player otherPlayer : Main.server.getOnlinePlayers()) {
                        if(!PlayersManager.isSamePlayer(otherPlayer, player))
                            otherPlayer.spawnParticle(particleType, location, intensity);
                    }
                }

            }
        }.runTaskTimer(Main.instance, 0L, Main.configFile.getInt("effects.player_with_unfinished_task.tick_delay"));
    }
}
