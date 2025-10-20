package org.shonivergames.secretlife.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.KillManager;
import org.shonivergames.secretlife.LivesManager;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player player = event.getEntity();

        Player killer = event.getEntity().getKiller();
        if(killer != null && killer != player)
            KillManager.CommitKill(killer);

        LivesManager.removeLife(player);
    }
}
