package org.shonivergames.secretlife.events;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.shonivergames.secretlife.InteractionsManager;

public class PlayerInteractEvent implements Listener {
    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block == null || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        InteractionsManager.checkInteraction(event);
    }
}
