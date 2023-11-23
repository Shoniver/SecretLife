package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.LivesManager;

public class AddLife extends CommandBase{
    protected AddLife() {
        super("AddLife", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        LivesManager.addLife(player);
        printFeedback(sender);
    }
}