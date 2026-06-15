package com.azka.plugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

    public static Economy economy = null;
    private DatabaseManager databaseManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getLogger().info("MANTAP JIR! Plugin buatan kita berhasil aktif dengan Vault!");
        
        // Initialize and setup database manager
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.connect();
        this.databaseManager.setupTable();
        
        // Initialize scoreboard manager
        this.scoreboardManager = new ScoreboardManager(this);
        
        // Register command
        this.getCommand("shop").setExecutor(new ShopCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopTransactionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Periodic task to refresh scoreboards for all online players every 5 seconds (100 ticks)
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                this.scoreboardManager.updateScoreboard(player);
            }
        }, 100L, 100L);
    }

    @Override
    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }
        getLogger().info("Plugin dimatikan.");
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
