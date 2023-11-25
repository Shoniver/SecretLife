package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.LivesManager;

public class RemoveLife extends _CommandBase {
    public RemoveLife() {
        super("RemoveLife", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        LivesManager.removeLife(player);
        printFeedback(sender);
    }
}