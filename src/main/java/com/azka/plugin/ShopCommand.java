package com.azka.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cHanya pemain yang bisa membuka toko!");
            return true;
        }

        Player player = (Player) sender;

        // Sub-command: /shop sell → langsung ke menu jual semua
        if (args.length > 0 && args[0].equalsIgnoreCase("sell")) {
            player.openInventory(SellAllGUI.build(player));
            player.sendMessage("§a✦ §fMembuka menu §aJual Semua§f...");
            return true;
        }

        // Default: /shop → main menu
        player.openInventory(MainMenuGUI.build());
        player.sendMessage("§6✦ §fMembuka §6Toko Azka§f...");
        return true;
    }
}
