package com.azka.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final App plugin;

    public PlayerJoinListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        plugin.getScoreboardManager().updateScoreboard(player);

        if (plugin.getDatabaseManager() != null && plugin.getDatabaseManager().isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = plugin.getDatabaseManager().getDiamondCount(playerName);
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getScoreboardManager().setDiamondCount(playerName, count);
                    if (player.isOnline()) {
                        plugin.getScoreboardManager().updateScoreboard(player);
                    }
                });
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getScoreboardManager().removePlayer(event.getPlayer().getName());
    }
}
