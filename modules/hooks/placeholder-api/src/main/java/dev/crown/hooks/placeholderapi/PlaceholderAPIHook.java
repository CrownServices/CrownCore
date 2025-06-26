package dev.crown.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    JavaPlugin plugin;
    Map<String, String> placeholders = new HashMap<>();

    public PlaceholderAPIHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlaceholder(String identifier, String replacement) {
        placeholders.put(identifier, replacement);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "crowncore";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        // Check if the placeholder exists
        if (placeholders.containsKey(params)) {
            // Return the replacement value for the placeholder
            return placeholders.get(params);
        }
        // If the placeholder does not exist, return null
        return null;
    }
}
