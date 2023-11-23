package org.shonivergames.secretlife.events;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.shonivergames.secretlife.Main;

import java.util.List;

public class EntityRegenEvent implements Listener {
    public EntityRegenEvent(){
        setNaturalRegen(false);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player)
            event.setCancelled(true);
    }

    public static void setNaturalRegen(boolean isTrue){
        List<World> worlds = Main.server.getWorlds();
        for (World x: worlds) {
            x.setGameRule(GameRule.NATURAL_REGENERATION, isTrue);
        }
    }
}
