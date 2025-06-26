package dev.crown.core;

import org.bukkit.plugin.java.JavaPlugin;

public class CrownCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("CrownCore has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("CrownCore has been disabled.");
    }
}
