package by.lemooor;

import by.lemooor.event.PlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author lemooor xx.11.2020
 * @project SkyWars
 */

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(), this);
    }
}