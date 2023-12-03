package org.shonivergames.secretlife.config_readers;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class SoundEffectReader {
    private static final String configName = ".sound_effects.";

    public static void playAtPlayer(String configTitle, String configVar, Player player, boolean playPublicly){
        if(!Util.isPlayerOnline(player))
            return;

        String configPath = configTitle + configName + configVar;
        String sound = Main.configFile.getString(configPath + ".type");
        if(sound.isEmpty())
            return;
        float volume = (float) Main.configFile.getDouble(configPath + ".volume", 1);
        float pitch = (float) Main.configFile.getDouble(configPath + ".pitch", 1);
        if(playPublicly)
            player.getWorld().playSound(player, sound, SoundCategory.MASTER, volume, pitch);
        else
            player.playSound(player, sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void playAtLocation(String configTitle, String configVar, Location location){
        String configPath = configTitle + configName + configVar;
        String sound = Main.configFile.getString(configPath + ".type");
        if(sound.isEmpty())
            return;
        float volume = (float) Main.configFile.getDouble(configPath + ".volume", 1);
        float pitch = (float) Main.configFile.getDouble(configPath + ".pitch", 1);
        location.getWorld().playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }
}
