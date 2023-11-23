package org.shonivergames.secretlife.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.HealthManager;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.Main;

public class EntityDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if(entity instanceof Player && killer != null)
            HealthManager.addHealth(killer, Main.configFile.getInt("settings.health_for_murder"), Main.configFile.getString("settings.admin_name"), true);
    }
}
