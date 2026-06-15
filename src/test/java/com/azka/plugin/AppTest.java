package com.azka.plugin;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import me.gypopo.economyshopgui.api.events.PostTransactionEvent;
import me.gypopo.economyshopgui.objects.ShopItem;
import me.gypopo.economyshopgui.util.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        // 1. Menginisialisasi server palsu menggunakan MockBukkit
        server = MockBukkit.mock();

        // 2. Membuat mock plugin dependensi (Vault dan EconomyShopGUI)
        Plugin vaultPlugin = MockBukkit.createMockPlugin("Vault");
        MockBukkit.createMockPlugin("EconomyShopGUI");

        // 3. Mock Economy menggunakan Java Dynamic Proxy agar setupEconomy() sukses
        Economy mockEconomy = (Economy) Proxy.newProxyInstance(
                Economy.class.getClassLoader(),
                new Class[]{Economy.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isEnabled") || method.getName().equals("has")) {
                        return true;
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType == boolean.class) {
                        return false;
                    } else if (returnType == double.class) {
                        return 0.0;
                    } else if (returnType == int.class) {
                        return 0;
                    }
                    return null;
                }
        );

        // Daftarkan mock Economy ke ServicesManager Bukkit
        server.getServicesManager().register(
                Economy.class,
                mockEconomy,
                vaultPlugin,
                ServicePriority.Normal
        );

        // 4. Memuat (load) plugin DemoPlugin kita ke server palsu
        MockBukkit.load(App.class);
    }

    @AfterEach
    public void tearDown() {
        // Menutup server palsu
        MockBukkit.unmock();
    }

    @Test
    public void testDiamondPurchaseBroadcast() {
        // Membuat pemain simulasi (mock player)
        PlayerMock player = server.addPlayer("AzkaPlayer");

        // Verifikasi scoreboard terbuat saat join
        Scoreboard board = player.getScoreboard();
        assertNotNull(board);
        Objective sidebar = board.getObjective(DisplaySlot.SIDEBAR);
        assertNotNull(sidebar);
        assertEquals("§6§l✦ TOKO AZKA ✦", sidebar.getDisplayName());

        // Membuat mock ShopItem menggunakan anonymous class agar mengembalikan DIAMOND
        ShopItem shopItem = new ShopItem() {
            @Override
            public ItemStack getItemToGive() {
                return new ItemStack(Material.DIAMOND);
            }
        };

        // Menembakkan (fire) PostTransactionEvent secara manual dengan parameter pembelian DIAMOND
        PostTransactionEvent event = new PostTransactionEvent(
                shopItem,
                player,
                64, // amount
                6400.0, // price
                Transaction.Type.BUY_SCREEN,
                Transaction.Result.SUCCESS
        );

        // Panggil event secara manual ke server palsu
        server.getPluginManager().callEvent(event);

        // Memverifikasi apakah pesan broadcast berhasil diterima oleh pemain (MockBukkit v4 style)
        String received = player.nextMessage();
        assertEquals("§a[Toko] §eAzkaPlayer §fbaru saja memborong §b64x Diamond§a! Mantap!", received);
    }

    @Test
    public void testShopOpensMainMenu() {
        // Membuat pemain simulasi (mock player)
        PlayerMock player = server.addPlayer("AzkaPlayer");

        // Simulasi pemain mengetik perintah "/shop"
        player.performCommand("shop");

        // Verifikasi pesan pembuka dikirim ke pemain
        String openMessage = player.nextMessage();
        assertEquals("§6✦ §fMembuka §6Toko Azka§f...", openMessage);

        // Verifikasi bahwa GUI inventory Main Menu terbuka dengan TokoHolder
        assertNotNull(player.getOpenInventory(), "Pemain tidak membuka inventory apapun!");
        assertTrue(player.getOpenInventory().getTopInventory().getHolder() instanceof TokoHolder, "Holder GUI salah!");

        // Verifikasi GuiType = MAIN_MENU
        TokoHolder holder = (TokoHolder) player.getOpenInventory().getTopInventory().getHolder();
        assertEquals(TokoHolder.GuiType.MAIN_MENU, holder.getGuiType(), "GuiType harus MAIN_MENU!");
    }
}
