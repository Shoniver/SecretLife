package org.shonivergames.secretlife;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.config_readers.SettingReader;

public class LivesManager {
    private static final String baseConfigPath = "lives_manager";

    public static void addLife(Player player){
        int lives = Main.playerData.getLivesCount(player) + 1;
        setCurrentLives(player, lives);
    }

    public static void removeLife(Player player){
        int lives = Main.playerData.getLivesCount(player) - 1;
        if(lives < 0)
            lives = 0; // can't remove any more lives!
        else if(lives >= 0) {
            // Summon lightning strike on the final death
            if(lives == 0)
                player.getWorld().strikeLightningEffect(player.getLocation());

            setCurrentLives(player, lives);
        }
    }

    public static void initPlayer(Player player){
        int lives;
        if(!Main.playerData.isPlayerRegistered(player))
            lives = SettingReader.getInt(baseConfigPath, "start_amount");
        else
            lives = Main.playerData.getLivesCount(player);
        setCurrentLives(player, lives);
    }

    public static void setCurrentLives(Player player, int lives){
        Main.playerData.setLivesCount(player, lives);

        // Change player name color to match the amount of lives they now have
        setPlayerColor(player, lives);

        // Send them a private message in chat, matching the amount of lives they have
        MessageReader.sendPrivate(baseConfigPath, "intro." + String.valueOf(lives), player, player.getName());

        if(lives == 0)
            player.setGameMode(GameMode.SPECTATOR);
        else
            player.setGameMode(GameMode.SURVIVAL);
    }

    private static void setPlayerColor(Player player, int lives){
        Scoreboard scoreboard = Main.server.getScoreboardManager().getMainScoreboard();

        // Add to new team + set tab to proper color
        String teamName;
        if(lives >= 4)
            teamName = "4+";
        else
            teamName = String.valueOf(lives);
        Team team = scoreboard.getTeam(teamName);
        team.addEntry(player.getName());
    }

    public static String getColoredPlayerName(Player player){
        int lives = Main.playerData.getLivesCount(player);
        String colorCode = switch (lives){
            case 3 -> "§a";
            case 2 -> "§e";
            case 1 -> "§c";
            case 0 -> "§8";
            default -> "§2"; // 4+
        };
        return colorCode + player.getDisplayName();
    }

    public static void createTeams(){
        Scoreboard scoreboard = Main.server.getScoreboardManager().getMainScoreboard();
        Team t = scoreboard.registerNewTeam("4+");
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
        scoreboard.getTeam("4+").unregister();
        scoreboard.getTeam("3").unregister();
        scoreboard.getTeam("2").unregister();
        scoreboard.getTeam("1").unregister();
        scoreboard.getTeam("0").unregister();
    }

    public static boolean isRedPlayer(Player player) {
        return Main.playerData.getLivesCount(player) == 1;
    }
    public static boolean isYellowPlayer(Player player) {
        return Main.playerData.getLivesCount(player) == 2;
    }
    public static boolean isThereRedPlayer() {
        for (Player player : Main.server.getOnlinePlayers()) {
            if(isRedPlayer(player))
                return true;
        }
        return false;
    }
    public static boolean isThereYellowPlayer() {
        for (Player player : Main.server.getOnlinePlayers()) {
            if(isYellowPlayer(player))
                return true;
        }
        return false;
    }
}
