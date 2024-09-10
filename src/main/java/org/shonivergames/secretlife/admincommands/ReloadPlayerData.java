package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class ReloadPlayerData extends _CommandBase {
    public ReloadPlayerData() {
        super("ReloadPlayerData", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean irrelevant2) {
        Main.playerData.loadData();
        printFeedback(sender);
    }
}