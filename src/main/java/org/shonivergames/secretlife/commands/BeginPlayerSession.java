package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.PlayersManager;
import org.shonivergames.secretlife.TasksManager;

public class BeginPlayerSession extends CommandBase{
    protected BeginPlayerSession() {
        super("BeginPlayerSession", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        if(!TasksManager.canGetNewTask(sender, player))
            return;

        PlayersManager.setPlayerValue(player, "can_give_heart", true);
        TasksManager.giveAnimatedTask(player, false);
        printFeedback(sender);
    }
}