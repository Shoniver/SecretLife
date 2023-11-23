package org.shonivergames.secretlife.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityDamageEvent implements Listener {
    @EventHandler
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = ((Player) entity);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getHealth() - event.getFinalDamage());
        }
    }
}
