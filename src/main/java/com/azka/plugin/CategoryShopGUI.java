package com.azka.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Builds a professional category shop page (54 slots).
 * Shows items with custom formatted hover tooltips and a back button.
 */
public class CategoryShopGUI {

    private static final String[] CATEGORY_TITLES = {
        "minerals", "farming", "wood", "combat"
    };
    private static final String[] CATEGORY_DISPLAY = {
        "§b§l⛏ Minerals", "§a§l🌾 Farming", "§6§l🪵 Kayu & Alam", "§c§l⚔ Combat Drops"
    };

    // Slots that hold shop items (skip border and navigation)
    private static final int[] ITEM_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    public static Inventory build(String categoryKey) {
        String title = getCategoryTitle(categoryKey);
        TokoHolder holder = new TokoHolder(TokoHolder.GuiType.CATEGORY, categoryKey);
        Inventory inv = Bukkit.createInventory(holder, 54, "§e§l✦ Toko §8| " + title);
        holder.setInventory(inv);

        // Fill background and border
        ItemStack border = MainMenuGUI.makeItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        ItemStack fill = MainMenuGUI.makeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", null);

        for (int i = 0; i < 54; i++) {
            if (isBorderSlot(i)) {
                inv.setItem(i, border);
            } else {
                inv.setItem(i, fill);
            }
        }

        // Back button (overwrites slot 45 border)
        ItemStack back = MainMenuGUI.makeItem(
            Material.ARROW,
            "§c§l◀ KEMBALI",
            Arrays.asList("§7Kembali ke menu utama")
        );
        inv.setItem(45, back);

        // Fill items
        List<ShopRegistry.ShopEntry> items = ShopRegistry.getCategory(categoryKey);
        for (int i = 0; i < items.size() && i < ITEM_SLOTS.length; i++) {
            ShopRegistry.ShopEntry entry = items.get(i);
            inv.setItem(ITEM_SLOTS[i], makeShopItemDisplay(entry));
        }

        return inv;
    }

    private static boolean isBorderSlot(int slot) {
        return slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8;
    }

    private static ItemStack makeShopItemDisplay(ShopRegistry.ShopEntry entry) {
        String buyLine  = entry.canBuy()  ? "§8■ §e$" + String.format("%,.2f", entry.buyPrice)  : "§8■ §cTidak bisa dibeli";
        String sellLine = entry.canSell() ? "§8■ §a$" + String.format("%,.2f", entry.sellPrice) : "§8■ §cTidak bisa dijual";

        return MainMenuGUI.makeItem(entry.material, entry.displayName, Arrays.asList(
            "§7Kategori: " + getCategoryDisplayName(entry.category),
            "",
            "§a§lHarga Beli",
            buyLine,
            "",
            "§c§lHarga Jual",
            sellLine,
            "",
            "§e§lCara Interaksi:",
            " §7➔ §eKlik Kiri §7untuk beli 1 unit",
            " §7➔ §eKlik Kanan §7untuk beli 64 unit",
            " §7➔ §eShift + Klik Kiri §7untuk jual semua"
        ));
    }

    private static String getCategoryDisplayName(String categoryKey) {
        for (int i = 0; i < CATEGORY_TITLES.length; i++) {
            if (CATEGORY_TITLES[i].equalsIgnoreCase(categoryKey)) {
                return CATEGORY_DISPLAY[i].substring(4); // Remove color codes for pure text
            }
        }
        return "Toko";
    }

    private static String getCategoryTitle(String key) {
        for (int i = 0; i < CATEGORY_TITLES.length; i++) {
            if (CATEGORY_TITLES[i].equals(key)) return CATEGORY_DISPLAY[i];
        }
        return "§fShop";
    }
}
