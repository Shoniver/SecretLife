package org.shonivergames.secretlife;

import org.bukkit.Location;

import java.util.Random;

public class Util {
    private static Random rnd;

    public static boolean isSameLocation(Location loc1, Location loc2, boolean checkInt){
        if(checkInt)
            return (int)loc1.getX() == (int)loc2.getX() && (int)loc1.getY() == (int)loc2.getY() && (int)loc1.getZ() == (int)loc2.getZ();
        else
            return loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ();
    }

    public static Location getConfigLocation(String configPath){
        return new Location(Main.server.getWorlds().get(0), Main.configFile.getDouble(configPath + ".x"),
                Main.configFile.getDouble(configPath + ".y"),
                Main.configFile.getDouble(configPath + ".z"));
    }

    public static Location getRandomLocationBetween2(Location loc1, Location loc2){
        if(rnd == null)
            rnd = new Random();
        return new Location(null, rnd.nextDouble(loc1.getX(), loc2.getX()),
                rnd.nextDouble(loc1.getY(), loc2.getY()),
                rnd.nextDouble(loc1.getZ(), loc2.getZ()));
    }
}
