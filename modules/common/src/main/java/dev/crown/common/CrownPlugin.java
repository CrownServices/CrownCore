package dev.crown.common;

import dev.crown.configuration.model.Configuration;

public interface CrownPlugin {

    /**
     * Returns the name of the plugin.
     *
     * @return the name of the plugin
     */
    String getName();

    /**
     * Returns the version of the plugin.
     *
     * @return the version of the plugin
     */
    String getVersion();

    /**
     * Returns the main configuration for this plugin.
     *
     * @return the main configuration for this plugin
     */
    Configuration getConfiguration();

}
