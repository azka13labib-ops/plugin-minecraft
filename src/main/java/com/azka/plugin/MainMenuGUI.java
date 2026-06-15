package com.azka.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Builds the professional Main Menu GUI — 54 slots with black border,
 * light gray background, and beautiful category buttons.
 */
public class MainMenuGUI {

    public static Inventory build() {
        TokoHolder holder = new TokoHolder(TokoHolder.GuiType.MAIN_MENU);
        Inventory inv = Bukkit.createInventory(holder, 54, "§e§l✦ TOKO AZKA ✦");
        holder.setInventory(inv);

        // Fill background and border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        ItemStack fill = makeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", null);

        for (int i = 0; i < 54; i++) {
            if (isBorderSlot(i)) {
                inv.setItem(i, border);
            } else {
                inv.setItem(i, fill);
            }
        }

        // Category: Minerals (slot 10)
        inv.setItem(10, makeCategory(
            Material.DIAMOND,
            "§b§l⛏ KATEGORI MINERAL",
            Arrays.asList(
                "§7Jelajahi berbagai bijih, logam,",
                "§7dan permata berharga.",
                "",
                "§d§lItem Tersedia:",
                " §7• §fDiamond, Emerald, Netherite",
                " §7• §fGold Ingot, Iron Ingot, Coal",
                " §7• §fLapis Lazuli, Redstone...",
                "",
                "§e▶ Klik untuk membuka!"
            )
        ));

        // Category: Farming (slot 12)
        inv.setItem(12, makeCategory(
            Material.WHEAT,
            "§a§l🌾 KATEGORI PERTANIAN",
            Arrays.asList(
                "§7Beli dan jual hasil pertanian,",
                "§7tanaman, serta bahan pangan segar.",
                "",
                "§d§lItem Tersedia:",
                " §7• §fGandum, Wortel, Kentang",
                " §7• §fMelon, Labu, Sugar Cane",
                " §7• §fApel, Roti, Kue...",
                "",
                "§e▶ Klik untuk membuka!"
            )
        ));

        // Category: Wood & Nature (slot 14)
        inv.setItem(14, makeCategory(
            Material.OAK_LOG,
            "§6§l🪵 KATEGORI KAYU & ALAM",
            Arrays.asList(
                "§7Bahan bangunan dari kayu alami,",
                "§7kertas, dan barang dari alam.",
                "",
                "§d§lItem Tersedia:",
                " §7• §fOak, Birch, Spruce Log",
                " §7• §fOak Planks, Book, Paper",
                " §7• §fString, Charcoal, Stick...",
                "",
                "§e▶ Klik untuk membuka!"
            )
        ));

        // Category: Combat (slot 16)
        inv.setItem(16, makeCategory(
            Material.BLAZE_ROD,
            "§c§l⚔ KATEGORI COMBAT DROPS",
            Arrays.asList(
                "§7Dapatkan drop monster, bahan ramuan,",
                "§7dan barang jarahan langka.",
                "",
                "§d§lItem Tersedia:",
                " §7• §fBone, Arrow, Rotten Flesh",
                " §7• §fBlaze Rod, Blaze Powder",
                " §7• §fEnder Pearl, Nether Star...",
                "",
                "§e▶ Klik untuk membuka!"
            )
        ));

        // Sell All button (slot 31 - center of middle bottom area)
        ItemStack sellAll = makeItem(
            Material.GOLD_BLOCK,
            "§a§l💰 JUAL SEMUA ITEM TOKO",
            Arrays.asList(
                "§7Jual semua barang dari inventory Anda",
                "§7secara instan ke Toko Azka.",
                "",
                "§d§lFitur Toko:",
                " §7• §fMendeteksi semua item yang valid",
                " §7• §fMenghitung total harga secara aman",
                " §7• §fMenghindari exploit ekonomi",
                "",
                "§a▶ Klik untuk memindai inventory!"
            )
        );
        inv.setItem(31, sellAll);

        return inv;
    }

    private static boolean isBorderSlot(int slot) {
        return slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8;
    }

    public static ItemStack makeCategory(Material mat, String name, List<String> lore) {
        return makeItem(mat, name, lore);
    }

    public static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
