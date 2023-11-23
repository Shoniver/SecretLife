package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.TasksManager;

public class ResetPlayerTask extends CommandBase{
    protected ResetPlayerTask() {
        super("ResetTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        TasksManager.resetPlayerTask(player);
        printFeedback(sender);
    }
}