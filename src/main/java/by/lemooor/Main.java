package by.lemooor;

import by.lemooor.event.PlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.Configuration;

/**
 * @author lemooor xx.11.2020
 * @project SkyWars
 */

public class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        saveDefaultConfig();
        String config = getConfig().getString("Mongo");

        Bukkit.getPluginManager().registerEvents(new PlayerEvent(config), this);
    }
}