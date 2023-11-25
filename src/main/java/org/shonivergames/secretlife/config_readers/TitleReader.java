package org.shonivergames.secretlife.config_readers;

import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class TitleReader {
    private static final String configName = ".title_messages.";

    public static void send(String configTitle, String configVar, Player target, String... formattingValues){
        String configPath = configTitle + configName + configVar;
        String title = Main.configFile.getString(configPath + ".title", null);
        String sub = Main.configFile.getString(configPath + ".sub", null);
        int fadeIn = Main.configFile.getInt(configPath + ".fadeIn", 10);
        int stay = Main.configFile.getInt(configPath + ".stay", 20);
        int fadeOut = Main.configFile.getInt(configPath + ".fadeOut", 10);
        title = Util.getFormattedString(title, formattingValues);
        sub = Util.getFormattedString(sub, formattingValues);

        target.resetTitle();
        target.sendTitle(title, sub, fadeIn, stay, fadeOut);
    }
}
