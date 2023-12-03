package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;

public class BeginPlayerSession extends _CommandBase {
    public BeginPlayerSession() {
        super("BeginPlayerSession", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        String errorCode = TasksManager.getBeginSessionError(player);
        if(errorCode != null) {
            MessageReader.sendPrivate(baseConfigPath, "specific_errors." + errorCode, sender, player.getName());
            return;
        }

        Main.playerData.setCanGift(player, true);
        if(TasksManager.getBeginSessionGiveTaskError(player) == null)
            TasksManager.giveTaskAnimated(player, false);
        printFeedback(sender);
    }
}