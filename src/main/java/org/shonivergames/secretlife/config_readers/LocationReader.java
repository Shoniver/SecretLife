package org.shonivergames.secretlife.config_readers;

import org.bukkit.Location;
import org.bukkit.World;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class LocationReader {
    private static final String configName = ".locations.";

    public static Location get(String configTitle, String configVar){
        String configPath = configTitle + configName + configVar;

        String worldName = Main.configFile.getString(configPath + ".world", "world");
        World world = Main.server.getWorld(worldName);
        if(world == null)
        {
            Main.logger.info("Couldn't find a world by the name " + worldName + ", assigning default world instead.");
            world = Main.server.getWorlds().get(0);
        }

        return new Location(world,
                Main.configFile.getDouble(configPath + ".x"),
                Main.configFile.getDouble(configPath + ".y"),
                Main.configFile.getDouble(configPath + ".z"));
    }

    public static Location getRandomLocation(String configTitle, String configVar){
        Location loc1 = get(configTitle, configVar + ".start");
        Location loc2 = get(configTitle, configVar + ".end");

        return new Location(loc1.getWorld(), Util.getRandomDoubleInRange(loc1.getX(), loc2.getX()),
                Util.getRandomDoubleInRange(loc1.getY(), loc2.getY()),
                Util.getRandomDoubleInRange(loc1.getZ(), loc2.getZ()));
    }

    public static boolean isAtLocation(String configTitle, String configVar, Location locToCheck){
        Location loc = get(configTitle, configVar);
        if(loc.getWorld() != locToCheck.getWorld())
            return false;

        return (int)Math.ceil(loc.getX()) == (int)Math.ceil(locToCheck.getX()) && (int)Math.ceil(loc.getY()) == (int)Math.ceil(locToCheck.getY()) && (int)Math.ceil(loc.getZ()) == (int)Math.ceil(locToCheck.getZ());
    }
}
