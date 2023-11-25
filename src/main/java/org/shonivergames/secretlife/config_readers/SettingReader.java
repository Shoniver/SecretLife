package org.shonivergames.secretlife.config_readers;

import org.shonivergames.secretlife.Main;

public class SettingReader {
    private static final String configName = ".settings.";

    public static String getAdminName(){
        return Main.configFile.getString("basic_settings.admin_name");
    }

    public static String getString(String configTitle, String configVar){
        return Main.configFile.getString(configTitle + configName + configVar);
    }

    public static int getInt(String configTitle, String configVar){
        return Main.configFile.getInt(configTitle + configName + configVar);
    }

    public static double getDouble(String configTitle, String configVar){
        return Main.configFile.getDouble(configTitle + configName + configVar);
    }

    public static boolean getBool(String configTitle, String configVar){
        return Main.configFile.getBoolean(configTitle + configName + configVar);
    }
}
