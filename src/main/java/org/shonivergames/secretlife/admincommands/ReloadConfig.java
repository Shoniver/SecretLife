package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;

public class ReloadConfig extends _CommandBase {
    public ReloadConfig() {
        super("ReloadConfig", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant) {
        Main.loadConfig();
        printFeedback(sender);
    }
}