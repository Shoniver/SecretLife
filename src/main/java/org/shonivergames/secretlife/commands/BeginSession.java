package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.PlayersManager;
import org.shonivergames.secretlife.TasksManager;

public class BeginSession extends CommandBase{
    protected BeginSession() {
        super("_BeginSession", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant) {
        for (Player p : Main.server.getOnlinePlayers()) {
            if(!TasksManager.canGetNewTask(sender, p))
                return;
        }

        for (Player p : Main.server.getOnlinePlayers()) {
            PlayersManager.setPlayerValue(p, "can_give_heart", true);
            TasksManager.giveAnimatedTask(p, false);
        }

        printFeedback(sender);
    }
}
