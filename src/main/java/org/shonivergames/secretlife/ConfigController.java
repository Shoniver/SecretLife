package org.shonivergames.secretlife;

import org.shonivergames.secretlife.config_readers.SettingReader;

import java.io.File;

public class ConfigController {
    public static void loadConfig(){
        Main.instance.saveDefaultConfig();
        Main.instance.reloadConfig();
        Main.configFile = Main.instance.getConfig();
        if(!SettingReader.getConfigVersion().equals("1.0")){
            File file = new File(Main.instance.getDataFolder(), "config.yml");
            File config_old = new File(Main.instance.getDataFolder(), "config_old.yml");
            file.renameTo(config_old);
            Main.logger.info("Old config detected! Switching to a new file (your old file has been renamed 'config_old.yml').");
            loadConfig();
        }
    }
}
