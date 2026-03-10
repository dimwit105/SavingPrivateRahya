package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.gui;

import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.shield.ShieldModifier;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UpgradeShop
{
    private final VeryImportantParticipant vip;
    private static final Map<Material, Integer> GOLD_VALUES = new HashMap<>();

    static {
        // Basic Materials
        GOLD_VALUES.put(Material.GOLD_INGOT, 9);
        GOLD_VALUES.put(Material.GOLD_NUGGET, 1);
        GOLD_VALUES.put(Material.GOLD_BLOCK, 81); // 9 * 9

        GOLD_VALUES.put(Material.GOLDEN_SWORD, 18); // 2 * 9
        GOLD_VALUES.put(Material.GOLDEN_PICKAXE, 27); // 3 * 9
        GOLD_VALUES.put(Material.GOLDEN_SHOVEL, 9); // 1 * 9
        GOLD_VALUES.put(Material.GOLDEN_SPEAR, 9);
        GOLD_VALUES.put(Material.GOLDEN_AXE, 27); // 3 * 9
        GOLD_VALUES.put(Material.GOLDEN_HOE, 18); // 2 * 9

        GOLD_VALUES.put(Material.GOLDEN_CARROT, 8);
        GOLD_VALUES.put(Material.GOLDEN_APPLE, 8);
        GOLD_VALUES.put(Material.ENCHANTED_GOLDEN_APPLE, 648); // 9 * 9 * 8

        GOLD_VALUES.put(Material.GOLDEN_HELMET, 36); // 4 * 9
        GOLD_VALUES.put(Material.GOLDEN_CHESTPLATE, 72); // 8 * 9
        GOLD_VALUES.put(Material.GOLDEN_LEGGINGS, 63); // 7 * 9
        GOLD_VALUES.put(Material.GOLDEN_BOOTS, 36); // 4 * 9
    }

    public UpgradeShop(VeryImportantParticipant vip)
    {
        this.vip = vip;
    }
    public Gui getVIPGui()
    {
        Gui gui = Gui.gui().title(Component.text("Upgrade Shop")).rows(6).create();

        ItemStack goldNuggetForCounter = ItemStack.of(Material.GOLD_NUGGET);
        ItemMeta meta = goldNuggetForCounter.getItemMeta();
        meta.lore(List.of(Component.text("Total gold " + getGoldAvailable())));
        goldNuggetForCounter.setItemMeta(meta);

        GuiItem goldCounter = ItemBuilder.from(goldNuggetForCounter).asGuiItem();
        gui.setItem(0,0, goldCounter);

        fillAuraLevels(gui);


        return gui;
    }

    private void fillAuraLevels(Gui gui) {
        fillUpgradeRow(gui, vip.getAura().getLevel(), 2,
                Material.RED_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.ORANGE_STAINED_GLASS,
                level -> vip.getAura().levelUp()
        );
    }

    private void fillUpgradeRow(Gui gui, int currentLevel, int col, Material unlockedMat, Material canAffordMat, Material cantAffordMat, Consumer<Integer> onUpgrade) {
        for (int level = 1; level <= 6; level++) {
            final int levelToBuy = level;
            int cost = levelToBuy * 100;
            GuiItem guiItem;

            if (levelToBuy <= currentLevel) {
                // Already Unlocked
                guiItem = ItemBuilder.from(unlockedMat)
                        .name(Component.text("§cLevel " + levelToBuy + " Unlocked"))
                        .asGuiItem();
            }
            else if (levelToBuy == currentLevel + 1) {
                // Next Level
                boolean canAfford = getGoldAvailable() >= cost;
                Material mat = canAfford ? canAffordMat : cantAffordMat;

                guiItem = ItemBuilder.from(mat)
                        .name(Component.text("§aUnlock Level " + levelToBuy))
                        .lore(
                                Component.text("§7Cost: §6" + cost + " Gold"),
                                Component.text(canAfford ? "§eClick to Upgrade!" : "§cInsufficient Gold")
                        )
                        .asGuiItem(event -> {
                            if (getGoldAvailable() >= cost) {
                                consumeGoldFromInventory(cost);
                                onUpgrade.accept(levelToBuy); // Execute the specific upgrade logic

                                // Refresh both (or the whole GUI)
                                fillAuraLevels(gui);
                                gui.update();

                                vip.getPlayer().playSound(vip.getPlayer().getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                            }
                        });
            }
            else {
                guiItem = ItemBuilder.from(Material.TINTED_GLASS)
                        .name(Component.text("§8Level " + levelToBuy + " (Locked)"))
                        .asGuiItem();
            }

            gui.setItem(levelToBuy, col, guiItem);
        }
    }

    public int getGoldAvailable()
    {
        PlayerInventory inv = vip.getPlayer().getInventory();
        int total = 0;
        for(ItemStack is : inv.getStorageContents())
        {
            if(is == null)
                continue;

            if(GOLD_VALUES.containsKey(is.getType()))
            {
                total += GOLD_VALUES.get(is.getType())*is.getAmount();
            }
        }
        return total;
    }
    public void consumeGoldFromInventory(int cost) {
        PlayerInventory inv = vip.getPlayer().getInventory();

        List<Material> currency = List.of(
                Material.GOLD_BLOCK,
                Material.GOLD_INGOT,
                Material.GOLD_NUGGET
        );

        cost = processConsumption(inv, cost, currency);

        if (cost > 0) {
            List<Material> others = GOLD_VALUES.keySet().stream()
                    .filter(m -> !currency.contains(m))
                    .toList();

            cost = processConsumption(inv, cost, others);
        }

        if (cost < 0) {
            giveChange(vip.getPlayer(), Math.abs(cost));
        }
    }

    private void giveChange(Player player, int change)
    {
        int blocks = change / 81;
        change %= 81;
        int ingots = change/9 - blocks;
        change %= 9;
        int nuggets = change - blocks - ingots;
        if(blocks > 0) player.getInventory().addItem(ItemStack.of(Material.GOLD_BLOCK, blocks)).values().forEach(remaining -> player.getWorld().dropItem(player.getLocation(), remaining));;
        if(ingots > 0) player.getInventory().addItem(ItemStack.of(Material.GOLD_INGOT, ingots)).values().forEach(remaining -> player.getWorld().dropItem(player.getLocation(), remaining));;
        if(nuggets > 0) player.getInventory().addItem(ItemStack.of(Material.GOLD_NUGGET, nuggets)).values().forEach(remaining -> player.getWorld().dropItem(player.getLocation(), remaining));;
    }

    private int processConsumption(PlayerInventory inv, int cost, List<Material> targets) {
        ItemStack[] contents = inv.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            if (cost <= 0) break;

            ItemStack is = contents[i];
            if (is == null || !targets.contains(is.getType())) continue;

            int itemValue = GOLD_VALUES.get(is.getType());

            while (cost > 0 && is.getAmount() > 0) {
                is.setAmount(is.getAmount() - 1);
                cost -= itemValue;

                if (is.getAmount() <= 0) {
                    inv.setItem(i, null);
                    break;
                } else {
                    inv.setItem(i, is);
                }
            }
        }
        return cost;
    }
}
