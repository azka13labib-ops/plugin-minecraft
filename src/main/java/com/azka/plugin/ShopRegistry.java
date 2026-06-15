package com.azka.plugin;

import org.bukkit.Material;

import java.util.*;

/**
 * Registry semua item yang bisa dibeli/dijual di Toko Azka.
 * Setiap item memiliki harga beli (buyPrice) dan harga jual (sellPrice).
 */
public class ShopRegistry {

    public static class ShopEntry {
        public final Material material;
        public final String displayName;
        public final double buyPrice;   // Harga beli oleh player (-1 = tidak bisa dibeli)
        public final double sellPrice;  // Harga jual oleh player (-1 = tidak bisa dijual)
        public final String category;

        public ShopEntry(Material material, String displayName, double buyPrice, double sellPrice, String category) {
            this.material = material;
            this.displayName = displayName;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.category = category;
        }

        public boolean canBuy() { return buyPrice > 0; }
        public boolean canSell() { return sellPrice > 0; }
    }

    private static final Map<Material, ShopEntry> REGISTRY = new LinkedHashMap<>();
    private static final Map<String, List<ShopEntry>> CATEGORIES = new LinkedHashMap<>();

    static {
        // ═══════════════════════ MINERALS ═══════════════════════
        register(Material.COAL,              "§7Coal",              10,    5,    "minerals");
        register(Material.IRON_INGOT,        "§fIron Ingot",        25,    12,   "minerals");
        register(Material.GOLD_INGOT,        "§6Gold Ingot",        60,    30,   "minerals");
        register(Material.DIAMOND,           "§bDiamond",           250,   120,  "minerals");
        register(Material.EMERALD,           "§aEmerald",           300,   150,  "minerals");
        register(Material.LAPIS_LAZULI,      "§9Lapis Lazuli",      15,    7,    "minerals");
        register(Material.REDSTONE,          "§cRedstone",          12,    6,    "minerals");
        register(Material.NETHERITE_INGOT,   "§8Netherite Ingot",   1500,  700,  "minerals");
        register(Material.COPPER_INGOT,      "§6Copper Ingot",      8,     4,    "minerals");
        register(Material.AMETHYST_SHARD,    "§dAmethyst Shard",    20,    10,   "minerals");
        register(Material.QUARTZ,            "§fNether Quartz",     18,    9,    "minerals");
        register(Material.RAW_IRON,          "§fRaw Iron",          15,    7,    "minerals");
        register(Material.RAW_GOLD,          "§6Raw Gold",          35,    17,   "minerals");
        register(Material.RAW_COPPER,        "§6Raw Copper",        5,     2,    "minerals");

        // ═══════════════════════ FARMING ═══════════════════════
        register(Material.WHEAT,             "§eWheat",             5,     2,    "farming");
        register(Material.CARROT,            "§6Carrot",            6,     3,    "farming");
        register(Material.POTATO,            "§6Potato",            5,     2,    "farming");
        register(Material.BAKED_POTATO,      "§6Baked Potato",      8,     4,    "farming");
        register(Material.BEETROOT,          "§cBeetroot",          6,     3,    "farming");
        register(Material.MELON_SLICE,       "§aWatermelon",        4,     2,    "farming");
        register(Material.PUMPKIN,           "§6Pumpkin",           10,    5,    "farming");
        register(Material.SUGAR_CANE,        "§aSugar Cane",        4,     2,    "farming");
        register(Material.COCOA_BEANS,       "§6Cocoa Beans",       8,     4,    "farming");
        register(Material.BAMBOO,            "§aBamboo",            3,     1,    "farming");
        register(Material.APPLE,             "§cApple",             12,    6,    "farming");
        register(Material.BREAD,             "§6Bread",             15,    7,    "farming");
        register(Material.PUMPKIN_PIE,       "§6Pumpkin Pie",       18,    9,    "farming");
        register(Material.CAKE,              "§fCake",              80,    40,   "farming");

        // ═══════════════════════ WOOD & NATURE ═══════════════════════
        register(Material.OAK_LOG,           "§fOak Log",           8,     4,    "wood");
        register(Material.BIRCH_LOG,         "§fBirch Log",         8,     4,    "wood");
        register(Material.SPRUCE_LOG,        "§8Spruce Log",        8,     4,    "wood");
        register(Material.JUNGLE_LOG,        "§6Jungle Log",        8,     4,    "wood");
        register(Material.ACACIA_LOG,        "§6Acacia Log",        8,     4,    "wood");
        register(Material.DARK_OAK_LOG,      "§8Dark Oak Log",      8,     4,    "wood");
        register(Material.MANGROVE_LOG,      "§cMangrove Log",      8,     4,    "wood");
        register(Material.OAK_PLANKS,        "§fOak Planks",        4,     1,    "wood");
        register(Material.STICK,             "§fStick",             2,     -1,   "wood");
        register(Material.CHARCOAL,          "§8Charcoal",          8,     4,    "wood");
        register(Material.PAPER,             "§fPaper",             6,     3,    "wood");
        register(Material.BOOK,              "§fBook",              15,    7,    "wood");
        register(Material.STRING,            "§fString",            6,     3,    "wood");

        // ═══════════════════════ COMBAT & MOB DROPS ═══════════════════════
        register(Material.BONE,              "§fBone",              5,     2,    "combat");
        register(Material.ARROW,             "§fArrow",             3,     1,    "combat");
        register(Material.GUNPOWDER,         "§8Gunpowder",         15,    7,    "combat");
        register(Material.ROTTEN_FLESH,      "§2Rotten Flesh",      2,     1,    "combat");
        register(Material.SPIDER_EYE,        "§cSpider Eye",        12,    6,    "combat");
        register(Material.BLAZE_ROD,         "§6Blaze Rod",         40,    20,   "combat");
        register(Material.BLAZE_POWDER,      "§6Blaze Powder",      20,    8,    "combat");
        register(Material.ENDER_PEARL,       "§5Ender Pearl",       60,    30,   "combat");
        register(Material.GHAST_TEAR,        "§fGhast Tear",        120,   60,   "combat");
        register(Material.SLIME_BALL,        "§aSlime Ball",        20,    10,   "combat");
        register(Material.MAGMA_CREAM,       "§6Magma Cream",       25,    12,   "combat");
        register(Material.PHANTOM_MEMBRANE,  "§5Phantom Membrane",  80,    40,   "combat");
        register(Material.NETHER_STAR,       "§fNether Star",       5000,  2500, "combat");
        register(Material.DRAGON_BREATH,     "§5Dragon Breath",     500,   250,  "combat");
    }

    private static void register(Material mat, String name, double buy, double sell, String category) {
        ShopEntry entry = new ShopEntry(mat, name, buy, sell, category);
        REGISTRY.put(mat, entry);
        CATEGORIES.computeIfAbsent(category, k -> new ArrayList<>()).add(entry);
    }

    public static ShopEntry get(Material material) {
        return REGISTRY.get(material);
    }

    public static boolean canSell(Material material) {
        ShopEntry e = REGISTRY.get(material);
        return e != null && e.canSell();
    }

    public static double getSellPrice(Material material) {
        ShopEntry e = REGISTRY.get(material);
        return e != null ? e.sellPrice : 0.0;
    }

    public static List<ShopEntry> getCategory(String category) {
        return CATEGORIES.getOrDefault(category, Collections.emptyList());
    }

    public static Map<String, List<ShopEntry>> getAllCategories() {
        return CATEGORIES;
    }
}
