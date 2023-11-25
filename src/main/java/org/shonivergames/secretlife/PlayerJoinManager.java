package org.shonivergames.secretlife;

import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.LocationReader;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.config_readers.SettingReader;

public class PlayerJoinManager {
    private static final String baseConfigPath = "player_join_manager";

    public static void handlePlayerJoin(Player player){
        MessageReader.sendPrivate(baseConfigPath, "welcome", player, player.getName());

        if(Main.playerData.isPlayerRegistered(player)) {
            HealthManager.initNewPlayer(player);
            teleportToJoinPlatform(player);
        }

        LivesManager.initPlayer(player);
    }

    private static void teleportToJoinPlatform(Player player){
        if(!SettingReader.getBool(baseConfigPath, "teleport_to_platform_on_first_join"))
            return;

        player.teleport(LocationReader.get(baseConfigPath, "first_join_platform"));
    }
}
