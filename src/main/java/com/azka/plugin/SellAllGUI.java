package com.azka.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Builds the professional Sell All GUI — green borders, light gray background,
 * and beautiful item breakdown with a clear confirmation button.
 */
public class SellAllGUI {

    public static Inventory build(Player player) {
        TokoHolder holder = new TokoHolder(TokoHolder.GuiType.SELL_ALL);
        Inventory inv = Bukkit.createInventory(holder, 54, "§a§l💰 JUAL SEMUA ITEM");
        holder.setInventory(inv);

        // Fill background and border
        ItemStack border = MainMenuGUI.makeItem(Material.GREEN_STAINED_GLASS_PANE, " ", null);
        ItemStack fill = MainMenuGUI.makeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", null);

        for (int i = 0; i < 54; i++) {
            if (isBorderSlot(i)) {
                inv.setItem(i, border);
            } else {
                inv.setItem(i, fill);
            }
        }

        // Back button (overwrites slot 45 border)
        inv.setItem(45, MainMenuGUI.makeItem(
            Material.ARROW,
            "§c§l◀ KEMBALI",
            Arrays.asList("§7Kembali ke menu utama")
        ));

        // Calculate earnings and gather sellable items
        Map<Material, Integer> sellable = new LinkedHashMap<>();
        double total = 0.0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (ShopRegistry.canSell(item.getType())) {
                int prev = sellable.getOrDefault(item.getType(), 0);
                sellable.put(item.getType(), prev + item.getAmount());
                total += ShopRegistry.getSellPrice(item.getType()) * item.getAmount();
            }
        }

        final double finalTotal = total;

        if (sellable.isEmpty()) {
            // No sellable items (slot 22 Barrier)
            inv.setItem(22, MainMenuGUI.makeItem(
                Material.BARRIER,
                "§c§lTidak Ada Item untuk Dijual",
                Arrays.asList(
                    "§7Inventory Anda tidak memiliki item",
                    "§7yang bisa dijual di toko ini.",
                    "",
                    "§7Kumpulkan item dari kategori:",
                    "§8- §fMineral §7(Diamond, Iron, Gold...)",
                    "§8- §fPertanian §7(Gandum, Wortel...)",
                    "§8- §fKayu §7(Log, Planks...)",
                    "§8- §fCombat §7(Bone, Ender Pearl...)"
                )
            ));
        } else {
            // Show summary of sellable items (slots 10-16, 19-25, 28-34)
            int[] summarySlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
            };
            int idx = 0;
            for (Map.Entry<Material, Integer> e : sellable.entrySet()) {
                if (idx >= summarySlots.length) break;
                double itemEarning = ShopRegistry.getSellPrice(e.getKey()) * e.getValue();
                ShopRegistry.ShopEntry entry = ShopRegistry.get(e.getKey());
                String displayName = entry != null ? entry.displayName : e.getKey().name();

                inv.setItem(summarySlots[idx++], MainMenuGUI.makeItem(
                    e.getKey(),
                    displayName,
                    Arrays.asList(
                        "§7Rincian Jual:",
                        " §7• §fJumlah: §e" + e.getValue() + " unit",
                        " §7• §fHarga/unit: §a$" + String.format("%,.2f", ShopRegistry.getSellPrice(e.getKey())),
                        "",
                        "§d§lEstimasi Hasil",
                        "§8■ §e$" + String.format("%,.2f", itemEarning)
                    )
                ));
            }

            // Big CONFIRM button (slot 49, Gold Block)
            ItemStack confirm = MainMenuGUI.makeItem(
                Material.GOLD_BLOCK,
                "§a§l💰 KONFIRMASI JUAL SEMUA!",
                Arrays.asList(
                    "§7Klik untuk menjual semua item yang",
                    "§7tercantum di atas dari inventory Anda.",
                    "",
                    "§d§lRingkasan Penjualan:",
                    " §7• §fJenis Barang: §e" + sellable.size() + " jenis",
                    "",
                    "§a§lTotal Pendapatan",
                    "§8■ §e§l$" + String.format("%,.2f", finalTotal),
                    "",
                    "§a▶ Klik untuk memproses penjualan!"
                )
            );
            inv.setItem(49, confirm);
        }

        return inv;
    }

    private static boolean isBorderSlot(int slot) {
        return slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8;
    }
}
