package org.shonivergames.secretlife.config_readers;

import org.bukkit.Location;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class LocationReader {
    private static final String configName = ".locations.";

    public static Location get(String configTitle, String configVar){
        String configPath = configTitle + configName + configVar;
        return new Location(Main.server.getWorld(Main.configFile.getString(configPath + ".world", "world")),
                Main.configFile.getDouble(configPath + ".x"),
                Main.configFile.getDouble(configPath + ".y"),
                Main.configFile.getDouble(configPath + ".z"));
    }

    public static Location getRandomLocation(String configTitle, String configVar){
        Location loc1 = get(configTitle, configVar + ".start");
        Location loc2 = get(configTitle, configVar + ".end");

        return new Location(Main.server.getWorlds().get(0), Util.getRandomDoubleInRange(loc1.getX(), loc2.getX()),
                Util.getRandomDoubleInRange(loc1.getY(), loc2.getY()),
                Util.getRandomDoubleInRange(loc1.getZ(), loc2.getZ()));
    }

    public static boolean isAtLocation(String configTitle, String configVar, Location locToCheck, boolean checkInt){
        Location loc = get(configTitle, configVar);
        if(loc.getWorld() != locToCheck.getWorld())
            return false;
        if(checkInt)
            return (int)loc.getX() == (int)locToCheck.getX() && (int)loc.getY() == (int)locToCheck.getY() && (int)loc.getZ() == (int)locToCheck.getZ();
        else
            return loc.getX() == locToCheck.getX() && loc.getY() == locToCheck.getY() && loc.getZ() == locToCheck.getZ();
    }
}
