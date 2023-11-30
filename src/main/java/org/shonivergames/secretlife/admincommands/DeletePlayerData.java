package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class DeletePlayerData extends _CommandBase {
    public DeletePlayerData() {
        super("DeletePlayerData", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        Main.playerData.deletePlayerData(player);
        printFeedback(sender);
    }
}