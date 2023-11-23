package org.shonivergames.secretlife.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.HealthManager;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.PlayersManager;
import org.shonivergames.secretlife.TextManager;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TextManager.sendFormattedPrivateMessage(player, "messages.player.welcome", player, null);
        PlayersManager.initPlayer(player);
        HealthManager.initPlayer(player);
        LivesManager.initPlayer(player);
    }
}
