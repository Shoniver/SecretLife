package org.shonivergames.secretlife;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundManager {
    public static void playSoundEffect(Player player, Location location, String configVar, boolean isPublicSound){
        String sound = Main.configFile.getString("sound_effects." + configVar + ".name");
        float pitch = (float) Main.configFile.getDouble("sound_effects." + configVar + ".pitch");
        float volume = (float) Main.configFile.getDouble("sound_effects." + configVar + ".volume");
        if(isPublicSound)
            player.getWorld().playSound(location, sound, SoundCategory.MASTER, volume, pitch);
        else
            player.playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }
}
