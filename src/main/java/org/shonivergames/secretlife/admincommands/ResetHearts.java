package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;

public class ResetHearts extends _CommandBase {
    public ResetHearts() {
        super("ResetHearts", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        HealthManager.resetHealth(player);
        printFeedback(sender);
    }
}