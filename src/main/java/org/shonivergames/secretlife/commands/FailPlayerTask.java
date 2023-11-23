package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;

public class FailPlayerTask extends CommandBase{
    protected FailPlayerTask() {
        super("FailPlayerTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        if(!TasksManager.canRemoveTask(sender, player))
            return;

        TasksManager.failTask(player);
        printFeedback(sender);
    }}