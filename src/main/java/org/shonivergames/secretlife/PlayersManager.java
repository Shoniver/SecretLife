package org.shonivergames.secretlife;

import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.*;

public class PlayersManager {
    private static final String baseConfigPath = "players_manager";

    public static void handlePlayerJoin(Player player){
        player.resetTitle(); // If the player leaves with a title on their screen, it sometimes stays when they relog
        MessageReader.sendPrivate(baseConfigPath, "welcome", player, player.getName());

        if(!Main.playerData.isPlayerRegistered(player)) {
            HealthManager.initNewPlayer(player);
            teleportToJoinPlatform(player);
        }

        LivesManager.initPlayer(player);
        TasksManager.initPlayer(player);
    }

    private static void teleportToJoinPlatform(Player player){
        if(!SettingReader.getBool(baseConfigPath, "teleport_to_platform_on_first_join"))
            return;

        player.teleport(LocationReader.get(baseConfigPath, "starting_platform"));
    }
}
