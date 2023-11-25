package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.PlayerDataManager;

public class BeginPlayerSession extends _CommandBase {
    public BeginPlayerSession() {
        super("BeginPlayerSession", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        String errorCode = TasksManager.getReceiveNewTaskError(player);
        if(errorCode != null) {
            MessageReader.sendPrivate(baseConfigPath, errorCode, sender);
            return;
        }

        Main.playerData.setCanGift(player, true);
        TasksManager.giveTaskAnimated(player, false);
        printFeedback(sender);
    }
}