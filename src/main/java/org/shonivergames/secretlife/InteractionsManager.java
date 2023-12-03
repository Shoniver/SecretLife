package org.shonivergames.secretlife;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.shonivergames.secretlife.config_readers.LocationReader;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.config_readers.SettingReader;
import org.shonivergames.secretlife.config_readers.SoundEffectReader;

public class InteractionsManager {
    private static final String baseConfigPath = "interactions_manger";

    public static void checkInteraction(PlayerInteractEvent event){
        Material correctBlockType = Material.getMaterial(SettingReader.getString(baseConfigPath, "block_type"));
        Block block = event.getClickedBlock();

        if(block.getType() == correctBlockType){
            Player player = event.getPlayer();

            Location blockLocation = event.getClickedBlock().getLocation();
            if(LocationReader.isAtLocation(baseConfigPath, "pass_task", blockLocation, true))
                passTaskInteraction(player);
            else if(LocationReader.isAtLocation(baseConfigPath, "fail_task", blockLocation, true))
                failTaskInteraction(player);
            else if(LocationReader.isAtLocation(baseConfigPath, "reroll_task", blockLocation, true))
                rerollTaskInteraction(player);
        }
    }

    public static void passTaskInteraction(Player player){
        String passTaskError = TasksManager.getPassTaskError(player);
        if(passTaskError != null) {
            MessageReader.sendPrivate(baseConfigPath, passTaskError, player);
            return;
        }
        SoundEffectReader.playAtPlayer(baseConfigPath, "pass_task", player, true);
        TasksManager.passTask(player);
    }

    public static void rerollTaskInteraction(Player player){
        String rerollTaskError = TasksManager.getRerollTaskError(player);
        if(rerollTaskError != null) {
            MessageReader.sendPrivate(baseConfigPath, rerollTaskError, player);
            return;
        }
        SoundEffectReader.playAtPlayer(baseConfigPath, "reroll_task", player, true);
        TasksManager.giveTaskAnimated(player, true);
    }

    public static void failTaskInteraction(Player player){
        String failTaskError = TasksManager.getFailTaskError(player);
        if(failTaskError != null) {
            MessageReader.sendPrivate(baseConfigPath, failTaskError, player);
            return;
        }
        SoundEffectReader.playAtPlayer(baseConfigPath, "fail_task", player, true);
        TasksManager.failTask(player, false);
    }
}
