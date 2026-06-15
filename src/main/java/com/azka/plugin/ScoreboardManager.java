package com.azka.plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager {

    private final Map<String, Integer> diamondCountCache = new ConcurrentHashMap<>();

    public ScoreboardManager(App plugin) {
    }

    public void setDiamondCount(String playerName, int count) {
        diamondCountCache.put(playerName, count);
    }

    public void incrementDiamondCount(String playerName, int amount) {
        int current = getCachedDiamondCount(playerName);
        diamondCountCache.put(playerName, current + amount);
    }

    public int getCachedDiamondCount(String playerName) {
        return diamondCountCache.getOrDefault(playerName, 0);
    }

    public void removePlayer(String playerName) {
        diamondCountCache.remove(playerName);
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        if (board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        Objective obj = board.getObjective("stats");
        if (obj != null) {
            obj.unregister();
        }

        obj = board.registerNewObjective("stats", Criteria.DUMMY, Component.text("§6§l✦ TOKO AZKA ✦"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        double balance = App.economy != null ? App.economy.getBalance(player) : 0.0;
        int diamonds = getCachedDiamondCount(player.getName());

        obj.getScore("§7---------------------").setScore(10);
        obj.getScore("§e✦ DATA PEMAIN").setScore(9);
        obj.getScore(" §7• §fNama: §b" + player.getName()).setScore(8);
        obj.getScore("   ").setScore(7);
        obj.getScore("§e✦ STATISTIK TOKO").setScore(6);
        obj.getScore(" §7• §fSaldo: §a$§f" + String.format("%,.0f", balance)).setScore(5);
        obj.getScore(" §7• §fDiamond: §b💎 §f" + diamonds).setScore(4);
        obj.getScore("  ").setScore(3);
        obj.getScore("§7--------------------- ").setScore(2);
        obj.getScore("§d§lplay.azkamc.net").setScore(1);
    }
}
