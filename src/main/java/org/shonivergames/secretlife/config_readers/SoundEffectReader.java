package org.shonivergames.secretlife.config_readers;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class SoundEffectReader {
    private static final String configName = ".sound_effects.";

    public static void playAtPlayer(String configTitle, String configVar, Player player, boolean playPublicly){
        playAtEntity(configTitle, configVar, player, player, playPublicly);
    }

    public static void playAtEntity(String configTitle, String configVar, Player player, Entity entity, boolean playPublicly){
        String configPath = configTitle + configName + configVar;
        String sound = Main.configFile.getString(configPath + ".type");
        if(sound.isEmpty())
            return;
        float volume = (float) Main.configFile.getDouble(configPath + ".volume", 1);
        float pitch = (float) Main.configFile.getDouble(configPath + ".pitch", 1);
        if(playPublicly)
            player.getWorld().playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
        else
            player.playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void playAtLocation(String configTitle, String configVar, Player player, Location location, boolean playPublicly){
        String configPath = configTitle + configName + configVar;
        String sound = Main.configFile.getString(configPath + ".type");
        if(sound.isEmpty())
            return;
        float volume = (float) Main.configFile.getDouble(configPath + ".volume", 1);
        float pitch = (float) Main.configFile.getDouble(configPath + ".pitch", 1);
        if(playPublicly)
            player.getWorld().playSound(location, sound, SoundCategory.MASTER, volume, pitch);
        else
            player.playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }
}
