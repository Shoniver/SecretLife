package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;

public class RemoveHeart extends _CommandBase {
    public RemoveHeart() {
        super("RemoveHeart", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        HealthManager.removeHealth(player, 2, false);
        printFeedback(sender);
    }
}