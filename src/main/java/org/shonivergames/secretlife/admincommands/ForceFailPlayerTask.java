package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;

public class ForceFailPlayerTask extends _CommandBase {
    public ForceFailPlayerTask() {
        super("ForceFailPlayerTask", true);
    }

    @Override
    public void executeCommand(CommandSender sender, Player player, boolean irrelevant) {
        String errorCode = TasksManager.getFailTaskError(player);
        if(errorCode != null) {
            MessageReader.sendPrivate(baseConfigPath, "specific_errors." + errorCode, sender, player.getName());
            return;
        }

        TasksManager.failTask(player);
        printFeedback(sender);
    }}