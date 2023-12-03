package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class ResetPlayerTask extends _CommandBase {
    public ResetPlayerTask() {
        super("ResetPlayerTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        Main.playerData.resetTask(player);
        printFeedback(sender);
    }
}