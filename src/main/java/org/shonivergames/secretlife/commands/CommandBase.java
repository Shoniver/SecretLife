package org.shonivergames.secretlife.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.TextManager;

public abstract class CommandBase {
    public String command;
    public boolean isPerPlayer = false;
    protected CommandBase(String command, boolean isPerPlayer){
        this.command = command;
        this.isPerPlayer = isPerPlayer;
    }

    public boolean isCorrectCommand(String arg){
        return arg.equalsIgnoreCase(command);
    }

    public abstract void executeCommand(CommandSender sender, Player player);

    protected void printFeedback(CommandSender sender){
        if(sender != null)
            TextManager.sendPrivateMessage(sender,"messages.commands.valid_cmd");
    }
}
