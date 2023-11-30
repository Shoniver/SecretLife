package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class DeleteAllPlayersData extends _CommandBase {
    public DeleteAllPlayersData() {
        super("DeleteAllPlayersData", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant) {
        Main.playerData.deleteAllData();
        printFeedback(sender);
    }
}