package org.shonivergames.secretlife;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class LivesManager {
    public static boolean addLife(Player player){
        int lives = getCurrentLives(player) + 1;
        setCurrentLives(player, lives);
        return true;
    }

    public static boolean removeLife(Player player){
        int lives = getCurrentLives(player) - 1;
        if(lives < 0)
            return false; // can't remove any more lives!
        setCurrentLives(player, lives);
        return true;
    }

    public static void initPlayer(Player player){
        int lives;
        if(PlayersManager.isNewPlayer(player))
            lives = Main.configFile.getInt("settings.base_lives");
        else
            lives = getCurrentLives(player);
        setCurrentLives(player, lives);
    }

    public static int getCurrentLives(Player player){
        return PlayersManager.getPlayerInt(player, "lives");
    }

    public static void setCurrentLives(Player player, int lives){
        PlayersManager.setPlayerValue(player, "lives", lives);

        // Change player name color to match the amount of lives they now have
        setPlayerColor(player, lives);

        // Send them a private message in chat, matching the amount of lives they have
        String messageConfigPath;
        switch (lives){
            case 2 -> messageConfigPath = "messages.player.death.two";
            case 1 -> messageConfigPath = "messages.player.death.one";
            case 0 -> messageConfigPath = "messages.player.death.zero";
            default -> messageConfigPath = null;
        }
        if(messageConfigPath != null)
            TextManager.sendFormattedPrivateMessage(player, messageConfigPath, player, null);

        if(lives == 0)
            player.setGameMode(GameMode.SPECTATOR);
        else
            player.setGameMode(GameMode.SURVIVAL);
    }

    private static void setPlayerColor(Player player, int lives){
        Scoreboard scoreboard = Main.server.getScoreboardManager().getMainScoreboard();

        // Add to new team + set tab to proper color
        String teamName = String.valueOf(lives);
        Team team = scoreboard.getTeam(teamName);
        team.addEntry(player.getName());
    }

    public static void createTeams(){
        Scoreboard scoreboard = Main.server.getScoreboardManager().getMainScoreboard();
        Team t = scoreboard.registerNewTeam("4");
        t.setColor(ChatColor.DARK_GREEN);
        t = scoreboard.registerNewTeam("3");
        t.setColor(ChatColor.GREEN);
        t = scoreboard.registerNewTeam("2");
        t.setColor(ChatColor.YELLOW);
        t = scoreboard.registerNewTeam("1");
        t.setColor(ChatColor.RED);
        t = scoreboard.registerNewTeam("0");
        t.setColor(ChatColor.DARK_GRAY);
    }

    public static void deleteTeams(){
        Scoreboard scoreboard = Main.server.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeam("4").unregister();
        scoreboard.getTeam("3").unregister();
        scoreboard.getTeam("2").unregister();
        scoreboard.getTeam("1").unregister();
        scoreboard.getTeam("0").unregister();
    }
}
