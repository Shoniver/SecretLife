package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.MessageReader;

public abstract class _CommandBase {
    protected final String baseConfigPath = "admin_commands_manager";

    public String command;
    public boolean isPerPlayer = false;
    protected _CommandBase(String command, boolean isPerPlayer){
        this.command = command;
        this.isPerPlayer = isPerPlayer;
    }

    public boolean isCorrectCommand(String arg){
        return arg.equalsIgnoreCase(command);
    }

    public abstract void executeCommand(CommandSender sender, Player player);

    protected void printFeedback(CommandSender sender){
        if(sender != null)
            MessageReader.sendPrivate(baseConfigPath, "success", sender);
    }
}
