package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.LivesManager;

public class AddLife extends _CommandBase {
    public AddLife() {
        super("AddLife", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        LivesManager.addLife(player);
        printFeedback(sender);
    }
}