package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;
import org.shonivergames.secretlife.Main;

public class RemoveHeart extends CommandBase{
    protected RemoveHeart() {
        super("RemoveHeart", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        HealthManager.removeHealth(player, 2, Main.configFile.getString("settings.admin_name"));
        printFeedback(sender);
    }
}