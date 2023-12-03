package org.shonivergames.secretlife.config_readers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

public class MessageReader {
    private static final String configName = ".messages.";

    public static void sendPrivate(String configTitle, String configVar, CommandSender target, String... formattingValues){
        if(target instanceof Player && !Util.isPlayerOnline((Player)target))
            return;

        String message = Main.configFile.getString(configTitle + configName + configVar);
        message = Util.getFormattedString(message, formattingValues);

        if(message == null || message.isEmpty())
            return;
        target.sendMessage(message);
    }

    public static void sendPublic(String configTitle, String configVar, String... formattingValues){
        String message = Main.configFile.getString(configTitle + configName + configVar);
        message = Util.getFormattedString(message, formattingValues);

        if(message == null || message.isEmpty())
            return;
        Main.server.broadcastMessage(message);
    }
}
