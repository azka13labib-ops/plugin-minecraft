package com.azka.plugin;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TokoHolder implements InventoryHolder {
    private Inventory inventory;
    private GuiType guiType;
    private String categoryKey; // Used for CATEGORY type

    public enum GuiType {
        MAIN_MENU,
        CATEGORY,
        SELL_ALL
    }

    public TokoHolder(GuiType guiType) {
        this.guiType = guiType;
    }

    public TokoHolder(GuiType guiType, String categoryKey) {
        this.guiType = guiType;
        this.categoryKey = categoryKey;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
