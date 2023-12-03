package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.PluginMenuManager;

public class Menu extends _CommandBase {
    public Menu() {
        super("_Menu", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean irrelevant2) {
        PluginMenuManager.showMenu((Player)sender);
        printFeedback(sender);
    }
}