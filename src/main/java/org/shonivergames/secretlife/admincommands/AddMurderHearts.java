package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.KillManager;

public class AddMurderHearts extends _CommandBase {
    public AddMurderHearts() {
        super("AddMurderHearts", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        KillManager.CommitKill(player);
        printFeedback(sender);
    }
}