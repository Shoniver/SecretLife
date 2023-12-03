package org.shonivergames.secretlife.admincommands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.PluginMenuManager;

import java.io.File;

public class StartNewSeason extends _CommandBase {
    public StartNewSeason() {
        super("StartNewSeason", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean isAfterWarning) {
        if(isAfterWarning || !(sender instanceof Player)) {
            Main.playerData.deleteAllData();
            deleteAllFiles("advancements");
            deleteAllFiles("playerdata");
            deleteAllFiles("stats");
            printFeedback(sender);
        }
        else
            PluginMenuManager.showWarningMenu((Player) sender, this);
    }

    private void deleteAllFiles(String folderName){
        for (World world : Main.server.getWorlds()) {
            File[] files = new File(world.getWorldFolder().getAbsolutePath() + "/" + folderName + "/").listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}