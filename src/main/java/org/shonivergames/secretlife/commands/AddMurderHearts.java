package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;
import org.shonivergames.secretlife.Main;

public class AddMurderHearts extends CommandBase{
    protected AddMurderHearts() {
        super("AddMurderHearts", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        HealthManager.addHealth(player, Main.configFile.getInt("settings.health_for_murder"), Main.configFile.getString("settings.admin_name"), true);
        printFeedback(sender);
    }
}