package org.shonivergames.secretlife;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.shonivergames.secretlife.events.*;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    public static Main instance;
    public static FileConfiguration configFile;
    public static PlayerDataManager playerData;
    public static Server server;
    public static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        loadConfig();

        playerData = new PlayerDataManager();
        logger = getLogger();
        server = getServer();

        getCommand(AdminCommandsManager.commandName).setExecutor(new AdminCommandsManager());
        getCommand(GiftCommand.commandName).setExecutor(new GiftCommand());

        server.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        server.getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        server.getPluginManager().registerEvents(new EntityDeathEvent(), this);
        server.getPluginManager().registerEvents(new PlayerRespawnEvent(), this);
        server.getPluginManager().registerEvents(new EntityDamageEvent(), this);
        server.getPluginManager().registerEvents(new PlayerInteractEvent(), this);

        LivesManager.createTeams();
        HealthManager.init();
        TasksManager.manageHasTaskEffect();
        HealthManager.handleTabListDisplay();

        logger.info("SecretLife has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LivesManager.deleteTeams();

        logger.info("SecretLife has been disabled!");
    }

    public static void loadConfig(){
        instance.reloadConfig();
        configFile = instance.getConfig();
    }
}
