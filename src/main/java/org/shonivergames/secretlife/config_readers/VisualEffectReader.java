package org.shonivergames.secretlife.config_readers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class VisualEffectReader {
    private static final String configName = ".visual_effects.";

    public static void play(String configTitle, String configVar, Player player, Location location, boolean showToSelf, boolean showToOtherPlayers){
        if(!Util.isPlayerOnline(player.getName()))
            return;

        String configPath = configTitle + configName + configVar;
        Particle particleType = Particle.valueOf(Main.configFile.getString(configPath + ".type"));
        int intensity = Main.configFile.getInt(configPath + ".intensity", 1);

        if(showToSelf)
            player.spawnParticle(particleType, location, intensity, 0, 0, 0, 0);
        if(showToOtherPlayers){
            for (Player otherPlayer : Main.server.getOnlinePlayers()) {
                if(!Util.isSamePlayer(otherPlayer, player))
                    otherPlayer.spawnParticle(particleType, location, intensity, 0, 0, 0, 0);
            }
        }
    }
}
