package org.shonivergames.secretlife.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.Util;

public class PlayerInteractEvent implements Listener {
    Location passTask;
    Location failTask;
    Location rerollTask;
    Material blockType;

    public PlayerInteractEvent(){
        passTask = Util.getConfigLocation("interactions.pass_task");
        failTask = Util.getConfigLocation("interactions.fail_task");
        rerollTask = Util.getConfigLocation("interactions.reroll_for_hard");
        blockType = Material.getMaterial(Main.configFile.getString("interactions.block_type"));
    }

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block == null || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if(block.getType() == blockType){
            Player player = event.getPlayer();

            if(!TasksManager.canPlayerInteract(null, player))
                return;

            Location blockLocation = event.getClickedBlock().getLocation();
            if(Util.isSameLocation(blockLocation, passTask, true)) {
                if (TasksManager.canRemoveTask(null, player))
                    TasksManager.passTask(player);
            }
            else if(Util.isSameLocation(blockLocation, failTask, true)) {
                if (TasksManager.canRemoveTask(null, player))
                    TasksManager.failTask(player);
            }
            else if(Util.isSameLocation(blockLocation, rerollTask, true)) {
                if (TasksManager.canRerollTask(null, player))
                    TasksManager.giveAnimatedTask(player, true);
            }
        }
    }
}
