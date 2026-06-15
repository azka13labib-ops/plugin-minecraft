package com.azka.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class DatabaseManager {

    private final App plugin;
    private Connection connection;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean enabled;

    public DatabaseManager(App plugin) {
        this.plugin = plugin;
        loadConfig();
    }

private void loadConfig() {
        plugin.saveDefaultConfig();
        // Wajib di-set ke true agar koneksinya menyala
        this.enabled = plugin.getConfig().getBoolean("mysql.enabled", true); 
        
        // Host cukup diisi "localhost" saja
        this.host = plugin.getConfig().getString("mysql.host", "localhost");
        
        // Tambahkan port 3306
        this.port = plugin.getConfig().getInt("mysql.port", 3306);
        
        this.database = plugin.getConfig().getString("mysql.database", "plugin_db");
        this.username = plugin.getConfig().getString("mysql.username", "root");
        this.password = plugin.getConfig().getString("mysql.password", "");
    }

    public synchronized boolean connect() {
        if (!enabled) {
            return false;
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
            
            // Try to load JDBC Drivers (MySQL CJ, legacy MySQL, or MariaDB as fallback)
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ignored) {
                    try {
                        Class.forName("org.mariadb.jdbc.Driver");
                    } catch (ClassNotFoundException ignored2) {
                        // Let DriverManager resolve the driver if classes are loaded automatically
                    }
                }
            }

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&connectTimeout=3000&socketTimeout=3000";
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("Successfully connected to MySQL database!");
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to MySQL database! Make sure credentials are correct.", e);
            return false;
        }
    }

    public synchronized void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing database connection", e);
        }
    }

    public void setupTable() {
        if (!enabled) return;
        
        try {
            if (!connect()) {
                return;
            }
            String sql = "CREATE TABLE IF NOT EXISTS shop_transactions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_name VARCHAR(64) NOT NULL," +
                    "item_name VARCHAR(64) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "price DOUBLE NOT NULL," +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
                plugin.getLogger().info("Table 'shop_transactions' checked/created successfully.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to setup database table", e);
        }
    }

    public synchronized void logTransaction(String playerName, String itemName, int quantity, double price) {
        if (!enabled) return;

        try {
            if (!connect()) {
                plugin.getLogger().warning("Could not log transaction: database connection is not active.");
                return;
            }
            String sql = "INSERT INTO shop_transactions (player_name, item_name, quantity, price) VALUES (?, ?, ?, ?);";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerName);
                stmt.setString(2, itemName);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, price);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to log transaction to database: " + e.getMessage());
        }
    }

    public synchronized int getDiamondCount(String playerName) {
        if (!enabled) return 0;
        int count = 0;
        try {
            if (!connect()) {
                return 0;
            }
            String sql = "SELECT SUM(quantity) FROM shop_transactions WHERE player_name = ? AND item_name = 'DIAMOND';";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get diamond count from database: " + e.getMessage());
        }
        return count;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
