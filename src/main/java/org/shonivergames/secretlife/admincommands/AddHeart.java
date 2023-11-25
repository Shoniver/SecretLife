package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.HealthManager;

public class AddHeart extends _CommandBase {
    public AddHeart() {
        super("AddHeart", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        HealthManager.addHealth(player, 2, false);
        printFeedback(sender);
    }
}
