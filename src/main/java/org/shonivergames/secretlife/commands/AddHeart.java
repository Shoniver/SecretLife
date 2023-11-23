package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;
import org.shonivergames.secretlife.Main;

public class AddHeart extends CommandBase{
    protected AddHeart() {
        super("AddHeart", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        HealthManager.addHealth(player, 2, Main.configFile.getString("settings.admin_name"), false);
        printFeedback(sender);
    }
}
