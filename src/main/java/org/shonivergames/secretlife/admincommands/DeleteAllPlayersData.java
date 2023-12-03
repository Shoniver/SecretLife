package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.PluginMenuManager;

public class DeleteAllPlayersData extends _CommandBase {
    public DeleteAllPlayersData() {
        super("DeleteAllPlayersData", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean isAfterWarning) {
        if(isAfterWarning || !(sender instanceof Player)) {
            Main.playerData.deleteAllData();
            printFeedback(sender);
        }
        else
            PluginMenuManager.showWarningMenu((Player) sender, this);
    }
}