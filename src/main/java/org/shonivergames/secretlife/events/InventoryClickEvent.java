package org.shonivergames.secretlife.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shonivergames.secretlife.PluginMenuManager;

public class InventoryClickEvent implements Listener {
    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        PluginMenuManager.handleInventoryInteractionEvent(event);
    }
}
