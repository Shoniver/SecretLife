package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;

public class BeginSession extends _CommandBase {
    public BeginSession() {
        super("_BeginSession", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean irrelevant2) {
        boolean needToExit = false;

        for (Player player : Main.server.getOnlinePlayers()) {
            String errorCode = TasksManager.getSessionBeginError(player);
            if(errorCode != null) {
                MessageReader.sendPrivate(baseConfigPath, "specific_errors." + errorCode, sender, player.getName());
                needToExit = true;
            }
        }

        if(needToExit)
            return;

        for (Player player : Main.server.getOnlinePlayers()) {
            Main.playerData.setCanGift(player, true);
            TasksManager.giveTaskAnimated(player, false);
        }

        TasksManager.handleStartOfSession();
        printFeedback(sender);
    }
}
