package org.shonivergames.secretlife;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class PlayersManager {
    private static Random rnd;

    public static void initPlayer(Player player){
        if(isNewPlayer(player) && Main.configFile.getBoolean("settings.first_join_platform.enabled"))
            player.teleport(Util.getConfigLocation("settings.first_join_platform.location"));
    }

    public static boolean isNewPlayer(Player player) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return !Main.configFile.contains("player_data." + currentUUID);
    }

    public static boolean isRedPlayer(Player player) {
        return getPlayerInt(player, "lives") == 1;
    }
    public static boolean isYellowPlayer(Player player) {
        return getPlayerInt(player, "lives") == 2;
    }

    public static <T> void setPlayerValue(Player player, String varName, T value) {
        String currentUUID = String.valueOf(player.getUniqueId());
        Main.configFile.set("player_data." + currentUUID + "." + varName, value);
        Main.instance.saveConfig();
    }

    public static int getPlayerInt(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return Main.configFile.getInt("player_data." + currentUUID + "." + varName);
    }

    public static String getPlayerString(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return Main.configFile.getString("player_data." + currentUUID + "." + varName);
    }

    public static boolean getPlayerBoolean(Player player, String varName) {
        String currentUUID = String.valueOf(player.getUniqueId());
        return Main.configFile.getBoolean("player_data." + currentUUID + "." + varName);
    }

    public static Player getRandomOtherPlayer(Player current) {
        if (rnd == null)
            rnd = new Random();
        Object[] allPlayers = Main.server.getOnlinePlayers().toArray();
        int randomIndex = rnd.nextInt(allPlayers.length);
        Player randomPlayer = (Player) allPlayers[randomIndex];

        if (randomPlayer.getName().equals(current.getName())) {
            randomIndex++;
            if (randomIndex >= allPlayers.length)
                randomIndex = 0;
        }
        return (Player) allPlayers[randomIndex];
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

    public static boolean isPlayerOnline(String playerName){
        return getPlayerFromName(playerName) != null;
    }

    public static Player getPlayerFromName(String playerName){
        return Main.server.getPlayerExact(playerName);
    }

    public static boolean isSamePlayer(Player player1, Player player2){
        return player1.getUniqueId().equals(player2.getUniqueId());
    }
}