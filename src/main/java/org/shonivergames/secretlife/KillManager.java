package org.shonivergames.secretlife;

import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.SettingReader;
import org.shonivergames.secretlife.config_readers.TitleReader;

public class KillManager {
    private static final String baseConfigPath = "kill_manager";
    public static void CommitKill(Player cause){
        boolean canNonRedsKill = SettingReader.getBool(baseConfigPath, "can_non_reds_kill");
        if(!canNonRedsKill && !LivesManager.isRedPlayer(cause))
            TitleReader.send(baseConfigPath, "non_red_kills_disabled", cause);
        else if(!cause.isDead()){
            int healthForKill = SettingReader.getInt(baseConfigPath, "health_reward");
            HealthManager.addHealthByPlayer(cause, healthForKill, SettingReader.getAdminName(), true);
        }
    }
}
