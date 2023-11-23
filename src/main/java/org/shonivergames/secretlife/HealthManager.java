package org.shonivergames.secretlife;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealthManager {
    public static int addHealth(Player player, int health, String givenBy, boolean overflow){
        if(health == 0)
            return 0;
        int maxHealth = 0;
        if(overflow)
            maxHealth = Main.configFile.getInt("settings.max_murder_health");
        else
            maxHealth = Main.configFile.getInt("settings.max_normal_health");
        int healthChange = health;
        double currentHealth = player.getHealth();

        if(currentHealth + healthChange > maxHealth) {
            healthChange =  maxHealth - (int)Math.round(currentHealth);
            currentHealth = maxHealth;
        }
        else
            currentHealth += healthChange;

        if (givenBy != null)
            givenBy = "Given by " + givenBy;
        sendTitle(player, healthChange, givenBy, "plus", "+");

        setHealth(player, currentHealth);

        return healthChange;
    }

    public static int removeHealth(Player player, int health, String takenBy){
        if(health == 0)
            return 0;
        int minHealth = 2;
        int healthChange = health;
        double currentHealth = player.getHealth();

        if(currentHealth < minHealth)
            healthChange = 0;
        else if(currentHealth - healthChange < minHealth) {
            healthChange =  (int)Math.round(currentHealth) - minHealth;
            currentHealth = minHealth;
        }
        else
            currentHealth -= healthChange;

        if(takenBy != null)
            takenBy = "Taken by " + takenBy;
        sendTitle(player, healthChange, takenBy, "minus", "-");

        setHealth(player,currentHealth);

        return healthChange;
    }

    private static void sendTitle(Player player, int healthChange, String subText, String type, String symbol){
        int stay = Main.configFile.getInt("settings.health_change_message_color.stay");
        int fade = Main.configFile.getInt("settings.health_change_message_color.fade");
        String colorCode = Main.configFile.getString("settings.health_change_message_color." + type);

        String title = colorCode + symbol;
        if(healthChange % 2 == 0)
            title += (healthChange / 2) + " hearts";
        else
            title += (healthChange / 2) + ".5 hearts";

        if(subText != null)
            subText = colorCode + subText;
        TextManager.sendFormattedTitle(player, title, subText, fade, stay, fade, null, null);
    }

    public static void resetHealth(Player player){
        setHealth(player,Main.configFile.getInt("settings.health_on_respawn"));
    }

    public static void initPlayer(Player player){
        if(PlayersManager.isNewPlayer(player))
            resetHealth(player);
    }

    public static boolean isOnMaxHealth(Player player){
        int maxHealth = Main.configFile.getInt("settings.max_normal_health");
        double currentHealth = player.getHealth();
        return maxHealth - (int)Math.round(currentHealth) == 0;
    }

    private static void setHealth(Player player, double health){
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        player.setHealth(health);
    }
}
