package org.shonivergames.secretlife;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.config_readers.SettingReader;
import org.shonivergames.secretlife.config_readers.SoundEffectReader;

import java.util.ArrayList;
import java.util.List;

public class GiftCommand implements TabCompleter, CommandExecutor {
    public final static String commandName = "gift";
    private final String baseConfigPath = "gift_command";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int healthAddition = SettingReader.getInt(baseConfigPath, "health_given");
        boolean canOverflow = SettingReader.getBool(baseConfigPath, "can_go_above_max");

        if (!(sender instanceof Player)) {
            MessageReader.sendPrivate(baseConfigPath, "errors.must_be_player", sender);
            return true;
        }

        if (args.length != 1) {
            MessageReader.sendPrivate(baseConfigPath, "errors.invalid_cmd_structure", sender);
            return true;
        }

        Player senderPlayer = (Player) sender;
        if (!Main.playerData.getCanGift(senderPlayer)) {
            MessageReader.sendPrivate(baseConfigPath, "errors.unavailable", sender);
            return true;
        }

        String playerName = args[0];
        if (!Util.isPlayerOnline(playerName)) {
            MessageReader.sendPrivate(baseConfigPath, "errors.no_player", sender, playerName);
            return true;
        }

        Player targetPlayer = Util.getPlayerFromName(playerName);
        if (Util.isSamePlayer(targetPlayer, senderPlayer)) {
            MessageReader.sendPrivate(baseConfigPath, "errors.self", sender);
            return true;
        }

        if (HealthManager.willGoAboveMaxHealth(targetPlayer, healthAddition, canOverflow)) {
            MessageReader.sendPrivate(baseConfigPath, "errors.will_go_above_max", sender, targetPlayer.getName());
            return true;
        }

        Main.playerData.setCanGift(senderPlayer, false);
        HealthManager.addHealthByPlayer(targetPlayer, healthAddition, senderPlayer.getName(), canOverflow);
        SoundEffectReader.playAtPlayer(baseConfigPath, "give", senderPlayer, false);
        SoundEffectReader.playAtPlayer(baseConfigPath, "receive", targetPlayer, false);
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
