package com.azka.plugin;

import me.gypopo.economyshopgui.api.events.PostTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ShopTransactionListener implements Listener {

    private final App plugin;

    public ShopTransactionListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostTransaction(PostTransactionEvent event) {
        // Cek jika tipe transaksi adalah BUY (membeli) menggunakan metode name() dari enum
        if (event.getTransactionType() != null && event.getTransactionType().name().startsWith("BUY")) {
            Player player = event.getPlayer();

            // Ambil ShopItem dari event
            if (event.getShopItem() != null) {
                ItemStack itemStack = event.getShopItem().getItemToGive();
                
                // Cek jika item yang dibeli bertipe DIAMOND
                if (itemStack != null && itemStack.getType() == Material.DIAMOND) {
                    int amount = event.getAmount();
                    double price = event.getPrice();
                    String playerName = player.getName();
                    String itemName = itemStack.getType().name();

                    // Kirim pesan selamat (broadcast) ke seluruh server
                    Bukkit.broadcastMessage("§a[Toko] §e" + playerName + " §fbaru saja memborong §b" + amount + "x Diamond§a! Mantap!");

                    // Update scoreboard stats and layout
                    plugin.getScoreboardManager().incrementDiamondCount(playerName, amount);
                    plugin.getScoreboardManager().updateScoreboard(player);

                    // Log transaksi ke database secara ASYNCHRONOUS agar tidak melambatkan server thread utama
                    if (plugin.getDatabaseManager() != null && plugin.getDatabaseManager().isEnabled()) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            plugin.getDatabaseManager().logTransaction(playerName, itemName, amount, price);
                        });
                    }
                }
            }
        }
    }
}
