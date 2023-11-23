package org.shonivergames.secretlife;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.shonivergames.secretlife.commands.CommandsManager;
import org.shonivergames.secretlife.commands.GiveHeart;
import org.shonivergames.secretlife.events.*;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    public static Main instance;
    public static FileConfiguration configFile;
    public static Server server;
    public static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        instance = this;

        configFile = getConfig();
        logger = getLogger();
        server = getServer();

        getCommand(CommandsManager.prefix).setExecutor(new CommandsManager());
        getCommand(GiveHeart.commandName).setExecutor(new GiveHeart());

        server.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        server.getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        server.getPluginManager().registerEvents(new EntityDeathEvent(), this);
        server.getPluginManager().registerEvents(new PlayerRespawnEvent(), this);
        server.getPluginManager().registerEvents(new EntityDamageEvent(), this);
        server.getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        if(configFile.getBoolean("settings.enable_uhc_mode"))
            server.getPluginManager().registerEvents(new EntityRegenEvent(), this);
        else
            EntityRegenEvent.setNaturalRegen(true);

        LivesManager.createTeams();
        TasksManager.manageHasTaskEffect();

        logger.info("SecretLife has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LivesManager.deleteTeams();

        logger.info("SecretLife has been disabled!");
    }
}
