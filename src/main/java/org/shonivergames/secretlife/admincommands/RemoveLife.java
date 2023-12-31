package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.TasksManager;

public class RemoveLife extends _CommandBase {
    public RemoveLife() {
        super("RemoveLife", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        LivesManager.removeLife(player);
        TasksManager.checkConstantTaskStatus(player);
        printFeedback(sender);
    }
}