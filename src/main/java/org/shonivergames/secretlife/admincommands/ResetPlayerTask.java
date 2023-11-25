package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;

public class ResetPlayerTask extends _CommandBase {
    public ResetPlayerTask() {
        super("ResetTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        Main.playerData.resetTask(player);
        printFeedback(sender);
    }
}