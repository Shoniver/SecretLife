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
        commandsList.add(new EndSession());
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
        commandsList.add(new StartNewSeason());
        commandsList.add(new Menu());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            PluginMenuManager.showMenu((Player)sender);
            return true;
        }
        if (args.length > 2) {
            MessageReader.sendPrivate(baseConfigPath, "generic_errors.invalid_cmd_structure", sender);
            return true;
        }

        _CommandBase requestedCommand = getCommand(args[0]);
        if(requestedCommand == null)
            MessageReader.sendPrivate(baseConfigPath, "generic_errors.invalid_cmd", sender);
        else{
            String player = null;
            if (args.length > 1)
                player = args[1];

            executeCommand(requestedCommand, sender, player, false);
        }
        return true;
    }

    public _CommandBase getCommand(String command){
        if(command == null)
            return null;
        for (_CommandBase currentCmd : commandsList) {
            if (currentCmd.isCorrectCommand(command))
                return currentCmd;
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<String>();

        // If the prefix isn't correct, don't bother continuing.
        if (!cmd.getName().equalsIgnoreCase(commandName))
            return options;

        // if we're on the first argument, just gotta give the list of all possible commands
        if (args.length == 1) {
            options = getAllCommandNames();
        }
        else if (args.length == 2) {
            // Find the command that was chosen in the first argument
            _CommandBase chosenCmd = getCommand(args[0]);
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

    public List<String> getAllCommandNames(){
        List<String> names = new ArrayList<String>();
        for (_CommandBase currentCmd : commandsList)
            names.add(currentCmd.name);
        return names;
    }

    public boolean executeCommand(_CommandBase command, CommandSender sender, String playerName, boolean isAfterWarning){
        // Fail-safe! Only OP players can execute commands.
        // This prevents the possibility of players making a "fake menu" by giving chests a custom name and thus gaining access to commands.
        if(sender instanceof Player && !sender.isOp()) {
            MessageReader.sendPrivate(baseConfigPath, "generic_errors.not_op", sender);
            return false;
        }

        if (command.isPerPlayer) {
            if(playerName == null)
                MessageReader.sendPrivate(baseConfigPath, "generic_errors.no_player_selected", sender, command.name);
            else if(!Util.isPlayerOnline(playerName))
                MessageReader.sendPrivate(baseConfigPath, "generic_errors.no_player", sender, playerName);
            else
                command.executeCommand(sender, Main.server.getPlayer(playerName), isAfterWarning);
            return false;
        }
        else {
            command.executeCommand(sender, null, isAfterWarning);
            return true;
        }
    }
}
