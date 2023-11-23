package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.LivesManager;

public class RemoveLife extends CommandBase{
    protected RemoveLife() {
        super("RemoveLife", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        LivesManager.removeLife(player);
        printFeedback(sender);
    }
}