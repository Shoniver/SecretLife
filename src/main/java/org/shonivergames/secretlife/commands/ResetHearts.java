package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;

public class ResetHearts extends CommandBase{
    protected ResetHearts() {
        super("ResetHearts", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        HealthManager.resetHealth(player);
        printFeedback(sender);
    }
}