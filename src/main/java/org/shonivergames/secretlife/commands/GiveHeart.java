package org.shonivergames.secretlife.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.*;

import java.util.ArrayList;
import java.util.List;

public class GiveHeart implements TabCompleter, CommandExecutor {
    public final static String commandName = "GiveHeart";
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            TextManager.sendPrivateMessage(sender, "messages.commands.must_be_player");
            return true;
        }

        if (args.length != 1) {
            TextManager.sendPrivateMessage(sender, "messages.commands.invalid_public_cmd_structure");
            return true;
        }

        Player senderPlayer = (Player) sender;
        if (!PlayersManager.getPlayerBoolean(senderPlayer, "can_give_heart")) {
            TextManager.sendPrivateMessage(sender, "messages.commands.give_heart.unavailable");
            return true;
        }

        String playerName = args[0];
        if (!PlayersManager.isPlayerOnline(playerName)) {
            TextManager.sendPrivateMessage(sender, "messages.commands.no_player");
            return true;
        }

        Player targetPlayer = PlayersManager.getPlayerFromName(playerName);
        if (PlayersManager.isSamePlayer(targetPlayer, senderPlayer)) {
            TextManager.sendPrivateMessage(sender, "messages.commands.give_heart.self");
            return true;
        }

        if (HealthManager.isOnMaxHealth(targetPlayer)) {
            TextManager.sendPrivateMessage(sender, "messages.commands.give_heart.already_full");
            return true;
        }

        PlayersManager.setPlayerValue(senderPlayer, "can_give_heart", false);
        HealthManager.addHealth(targetPlayer, 2, senderPlayer.getName(), false);
        SoundManager.playSoundEffect(senderPlayer, senderPlayer.getLocation(), "give_heart", false);
        SoundManager.playSoundEffect(targetPlayer, senderPlayer.getLocation(), "give_heart", false);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<String>();

        // If the prefix isn't correct, don't bother continuing.
        if (!cmd.getName().equalsIgnoreCase(commandName))
            return options;

        if (args.length == 1) {
            // Create a list of all currently-online players
            for (Player player : Main.server.getOnlinePlayers())
                options.add(player.getName());
        }
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
