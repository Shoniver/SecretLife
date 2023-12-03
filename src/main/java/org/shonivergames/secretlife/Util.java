package org.shonivergames.secretlife;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Util {
    private static Random rnd;

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

    public static boolean isPlayerOnline(String playerName){
        return getPlayerFromName(playerName) != null;
    }

    public static Player getPlayerFromName(String playerName){
        return Main.server.getPlayerExact(playerName);
    }

    public static boolean isSamePlayer(Player player1, Player player2){
        return player1.getUniqueId().equals(player2.getUniqueId());
    }

    public static String getFormattedString(String text, String... values){
        if(text == null || text.isEmpty())
            return text;

        String result = text;
        int i = 1;
        for (String value : values) {
            result = result.replace("<value" + i + ">", value);
            i++;
        }
        return result;
    }

    public static Double getRandomDoubleInRange(double start, double end){
        if (rnd == null)
            rnd = new Random();

        double min = Math.min(start, end);
        double max = Math.max(start, end);
        return start + (rnd.nextDouble() * (max - min));
    }

    public static Item spawnItemForPlayer(Player player, Location location, ItemStack itemStack){
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setVelocity(new Vector());
        item.setOwner(player.getUniqueId());
        item.setPickupDelay(0);
        return item;
    }

    public static String extractBookContent(BookMeta book){
        StringBuilder content = new StringBuilder();
        for (String page : book.getPages()) {
            content.append(page);
            content.append("\n");
        }
        return content.toString().trim();
    }
    public static void writeBookContent(BookMeta book, String content){
        String[] pageDividedContent = content.split("<p>");
        for (String pageContent : pageDividedContent)
            book.addPage(pageContent);
    }

    public static void openLink(String link) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            futureResponse.thenAccept(response -> {
                int statusCode = response.statusCode();
                if (statusCode < 200 || statusCode > 299)
                    Main.logger.info("Failed to open the given link! Status code: " + statusCode);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> safeGetStringListFromConfig(String configPath){
        List<String> list;
        if(Main.configFile.isList(configPath))
            list = Main.configFile.getStringList(configPath);
        else
            list = new ArrayList<>();
        return list;
    }
}
