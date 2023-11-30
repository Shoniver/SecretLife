package org.shonivergames.secretlife;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.admincommands.*;
import org.shonivergames.secretlife.config_readers.MessageReader;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandsManager implements TabCompleter, CommandExecutor {
    public static String commandName = "sl";
    List<_CommandBase> commandsList;

    private final String baseConfigPath = "admin_commands_manager";

    public AdminCommandsManager() {
        commandsList = new ArrayList<>();
        commandsList.add(new BeginSession());
        commandsList.add(new BeginPlayerSession());
        commandsList.add(new ReloadConfig());
        commandsList.add(new ForceFailPlayerTask());
        commandsList.add(new ResetPlayerTask());
        commandsList.add(new AddHeart());
        commandsList.add(new AddMurderHearts());
        commandsList.add(new RemoveHeart());
        commandsList.add(new ResetHearts());
        commandsList.add(new AddLife());
        commandsList.add(new RemoveLife());
        commandsList.add(new DeletePlayerData());
        commandsList.add(new DeleteAllPlayersData());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length > 2) {
            MessageReader.sendPrivate(baseConfigPath, "generic_errors.invalid_cmd_structure", sender);
            return true;
        }

        for (_CommandBase currentCmd : commandsList) {
            if (currentCmd.isCorrectCommand(args[0])) {
                String player = "";
                if (args.length > 1)
                    player = args[1];

                if (currentCmd.isPerPlayer && !Util.isPlayerOnline(player)) {
                    MessageReader.sendPrivate(baseConfigPath, "generic_errors.no_player", sender, player);
                }
                else
                    currentCmd.executeCommand(sender, Main.server.getPlayer(player));
                return true;
            }
        }

        MessageReader.sendPrivate(baseConfigPath, "generic_errors.invalid_cmd", sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<String>();

        // If the prefix isn't correct, don't bother continuing.
        if (!cmd.getName().equalsIgnoreCase(commandName))
            return options;

        // if we're on the first argument, just gotta give the list of all possible commands
        if (args.length == 1) {
            for (_CommandBase currentCmd : commandsList)
                options.add(currentCmd.command);
        }
        else if (args.length == 2) {
            // Find the command that was chosen in the first argument
            _CommandBase chosenCmd = null;
            for (_CommandBase currentCmd : commandsList) {
                if (currentCmd.isCorrectCommand(args[0])) {
                    chosenCmd = currentCmd;
                    break;
                }
            }
            // If there wasn't a valid command, we leave with an empty list.
            if(chosenCmd == null || !chosenCmd.isPerPlayer)
                return options;
            // Create a list of all currently-online players
            else {
                for (Player player : Main.server.getOnlinePlayers())
                    options.add(player.getName());
            }
        }
        // If there are more than 2 arguments, it is irrelevant
        else
            return options;


        List<String> result = new ArrayList<String>();
        for (String o : options) {
            if(o.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                result.add(o);
        }
        return  result;
    }
}
