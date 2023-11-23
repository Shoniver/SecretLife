package org.shonivergames.secretlife;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TextManager {
    public static void sendPrivateMessage(CommandSender target, String configPath){
        String message = Main.configFile.getString(configPath);
        if(message == null)
            return;
        target.sendMessage(message);
    }

    public static void sendFormattedPrivateMessage(CommandSender target, String configPath, Player player, String task){
        String message = Main.configFile.getString(configPath);
        if(message == null)
            return;
        message = getFormattedText(message, player, task);
        target.sendMessage(message);
    }

    public static void sendFormattedTitle(Player target, String header, String subtext, int fadeIn, int stay, int fadeOut, Player player, String task){
        target.resetTitle();
        header = getFormattedText(header, player, task);
        subtext = getFormattedText(subtext, player, task);
        target.sendTitle(header, subtext, fadeIn, stay, fadeOut);
    }

    public static void sendFormattedPublicMessage(String configPath, Player player, String task){
        String message = Main.configFile.getString(configPath);
        if(message == null)
            return;
        message = getFormattedText(message, player, task);
        Main.server.broadcastMessage(message);
    }

    public static String getFormattedText(String text, Player player, String task){
        if(player != null)
            text = text.replace(Main.configFile.getString("messages.player_indicator"), player.getName());
        if(task != null)
            text = text.replace(Main.configFile.getString("messages.task_indicator"), task);
        return text;
    }
}
