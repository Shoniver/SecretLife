package org.shonivergames.secretlife.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.PlayersManager;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        PlayersManager.handlePlayerJoin(event.getPlayer());
    }
}
