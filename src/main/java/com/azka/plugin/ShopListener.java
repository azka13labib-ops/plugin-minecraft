package com.azka.plugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ShopListener implements Listener {

    private final App plugin;

    public ShopListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == null || !(event.getInventory().getHolder() instanceof TokoHolder)) {
            return;
        }

        // Always cancel — prevents items being taken out of GUI
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        TokoHolder holder = (TokoHolder) event.getInventory().getHolder();

        ItemStack clicked = (event.getClickedInventory() != null)
            ? event.getClickedInventory().getItem(event.getSlot())
            : null;

        if (clicked == null || clicked.getType() == Material.AIR) return;

        switch (holder.getGuiType()) {
            case MAIN_MENU:
                handleMainMenu(player, clicked, event.getSlot());
                break;
            case CATEGORY:
                handleCategory(player, clicked, event.getSlot(), holder.getCategoryKey(), event.getClick());
                break;
            case SELL_ALL:
                handleSellAll(player, clicked, event.getSlot());
                break;
        }
    }

    // ─── MAIN MENU ────────────────────────────────────────────────────────────
    private void handleMainMenu(Player player, ItemStack clicked, int slot) {
        switch (slot) {
            case 10: player.openInventory(CategoryShopGUI.build("minerals")); break;
            case 12: player.openInventory(CategoryShopGUI.build("farming"));  break;
            case 14: player.openInventory(CategoryShopGUI.build("wood"));     break;
            case 16: player.openInventory(CategoryShopGUI.build("combat"));   break;
            case 31: player.openInventory(SellAllGUI.build(player));          break;
        }
    }

    // ─── CATEGORY SHOP ────────────────────────────────────────────────────────
    private void handleCategory(Player player, ItemStack clicked, int slot, String categoryKey, ClickType clickType) {
        // Back button
        if (slot == 45 && clicked.getType() == Material.ARROW) {
            player.openInventory(MainMenuGUI.build());
            return;
        }

        ShopRegistry.ShopEntry entry = ShopRegistry.get(clicked.getType());
        if (entry == null) return;

        if (clickType == ClickType.SHIFT_LEFT) {
            // Shift+Left = sell all of this type from inventory
            handleSellSpecific(player, clicked.getType());
            // Refresh the same category GUI
            player.openInventory(CategoryShopGUI.build(categoryKey));
            return;
        }

        int amount = (clickType == ClickType.RIGHT) ? 64 : 1;

        if (!entry.canBuy()) {
            player.sendMessage("§cItem ini tidak bisa dibeli.");
            return;
        }

        double cost = entry.buyPrice * amount;
        if (App.economy == null) {
            player.sendMessage("§cSistem ekonomi tidak tersedia!");
            return;
        }
        if (!App.economy.has(player, cost)) {
            player.sendMessage("§c✗ Uang tidak cukup! Dibutuhkan §e$" + String.format("%,.0f", cost) + "§c.");
            return;
        }

        App.economy.withdrawPlayer(player, cost);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(entry.material, amount));
        if (!leftover.isEmpty()) {
            // Drop what didn't fit
            leftover.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
        }

        String amountStr = amount == 1 ? "1x" : amount + "x";
        player.sendMessage("§a✔ Berhasil membeli §f" + amountStr + " " + entry.displayName
            + " §aseharga §e$" + String.format("%,.0f", cost) + "§a!");

        // Update scoreboard balance
        if (entry.material == Material.DIAMOND) {
            plugin.getScoreboardManager().incrementDiamondCount(player.getName(), amount);
        }
        plugin.getScoreboardManager().updateScoreboard(player);

        // Log to DB async
        if (plugin.getDatabaseManager() != null && plugin.getDatabaseManager().isEnabled()) {
            final double finalCost = cost;
            final int finalAmount = amount;
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseManager().logTransaction(player.getName(), entry.material.name(), finalAmount, finalCost)
            );
        }
    }

    // ─── SELL ALL ─────────────────────────────────────────────────────────────
    private void handleSellAll(Player player, ItemStack clicked, int slot) {
        if (slot == 45 && clicked.getType() == Material.ARROW) {
            player.openInventory(MainMenuGUI.build());
            return;
        }

        // Confirm button = GOLD_BLOCK at slot 49
        if (slot == 49 && clicked.getType() == Material.GOLD_BLOCK) {
            executeSellAll(player);
        }
    }

    // ─── SELL HELPERS ─────────────────────────────────────────────────────────
    private void executeSellAll(Player player) {
        double totalEarned = 0.0;
        int totalItems = 0;

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;
            if (!ShopRegistry.canSell(item.getType())) continue;

            double unitPrice = ShopRegistry.getSellPrice(item.getType());
            totalEarned += unitPrice * item.getAmount();
            totalItems += item.getAmount();
            contents[i] = null;
        }
        player.getInventory().setContents(contents);

        if (totalItems == 0) {
            player.sendMessage("§c✗ Tidak ada item yang bisa dijual di inventory kamu!");
            player.closeInventory();
            return;
        }

        App.economy.depositPlayer(player, totalEarned);

        player.sendMessage("");
        player.sendMessage("§6§l✦ §e§lTOKO AZKA §8- §aHasil Penjualan");
        player.sendMessage("§7  Total item terjual : §f" + totalItems);
        player.sendMessage("§7  Total pendapatan   : §a§l$" + String.format("%,.0f", totalEarned));
        player.sendMessage("§7  Saldo baru         : §e$" + String.format("%,.0f", App.economy.getBalance(player)));
        player.sendMessage("");

        plugin.getScoreboardManager().updateScoreboard(player);
        player.closeInventory();
    }

    private void handleSellSpecific(Player player, Material material) {
        double total = 0.0;
        int count = 0;

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != material) continue;
            total += ShopRegistry.getSellPrice(material) * item.getAmount();
            count += item.getAmount();
            contents[i] = null;
        }
        player.getInventory().setContents(contents);

        if (count == 0) {
            player.sendMessage("§c✗ Tidak ada §f" + material.name() + " §cdi inventory kamu.");
            return;
        }

        App.economy.depositPlayer(player, total);
        ShopRegistry.ShopEntry entry = ShopRegistry.get(material);
        String name = entry != null ? entry.displayName : "§f" + material.name();
        player.sendMessage("§a✔ Berhasil menjual §f" + count + "x " + name
            + " §aseharga §e$" + String.format("%,.0f", total) + "§a!");
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    @EventHandler
    public void onCommandSend(org.bukkit.event.player.PlayerCommandSendEvent event) {
        if (event.getPlayer().isOp()) return; // Don't hide from OPs

        event.getCommands().removeIf(cmd -> 
            cmd.contains(":") || // Hide all namespaced commands (e.g. essentials:fly)
            cmd.equalsIgnoreCase("tab") ||
            cmd.equalsIgnoreCase("papi") ||
            cmd.equalsIgnoreCase("placeholderapi") ||
            cmd.equalsIgnoreCase("economyshopgui") ||
            cmd.equalsIgnoreCase("esgui") ||
            cmd.equalsIgnoreCase("fadah") ||
            cmd.equalsIgnoreCase("ah") ||
            cmd.equalsIgnoreCase("jobs") ||
            cmd.equalsIgnoreCase("cmilib") ||
            cmd.equalsIgnoreCase("vault")
        );
    }
}
