package org.shonivergames.secretlife.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.HealthManager;

public class PlayerRespawnEvent implements Listener {
    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        HealthManager.resetHealth(player);
    }
}