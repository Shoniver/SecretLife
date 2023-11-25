package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;

public class FailPlayerTask extends _CommandBase {
    public FailPlayerTask() {
        super("FailPlayerTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player) {
        String errorCode = TasksManager.getRemoveTaskError(player);
        if(errorCode != null) {
            MessageReader.sendPrivate(baseConfigPath, "specific_errors." + errorCode, sender, player.getName());
            return;
        }

        TasksManager.failTask(player);
        printFeedback(sender);
    }}