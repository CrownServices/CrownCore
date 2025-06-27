package dev.crown.core;

import dev.crown.common.CrownPlugin;
import dev.crown.configuration.model.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class CrownCore extends JavaPlugin implements CrownPlugin {

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

    /**
     * {@inheritDoc}
     * @return the name of the plugin
     */
    @Override
    public String getVersion() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return the main configuration for this plugin
     */
    @Override
    public Configuration getConfiguration() {
        return null;
    }
}
