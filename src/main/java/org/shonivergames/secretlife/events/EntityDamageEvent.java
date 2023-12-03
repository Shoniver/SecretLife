package org.shonivergames.secretlife.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.HealthManager;

public class EntityDamageEvent implements Listener {
    @EventHandler
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            HealthManager.handleDamageEvent(event);
    }
}
