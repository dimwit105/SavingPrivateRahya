package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.Enderwolf;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class VerySuspiciousStew extends AbstractAbility
{
    private final int smallEffectDuration = 300*20;
    private final int bigEffectDuration = 120*20;
    private final List<SuspiciousEffectEntry> effects = List.of(
            SuspiciousEffectEntry.create(PotionEffectType.STRENGTH, smallEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.WEAKNESS, smallEffectDuration),

            SuspiciousEffectEntry.create(PotionEffectType.REGENERATION, bigEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.WITHER, bigEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.POISON, bigEffectDuration),

            SuspiciousEffectEntry.create(PotionEffectType.HUNGER, bigEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.SATURATION, bigEffectDuration),

            SuspiciousEffectEntry.create(PotionEffectType.BLINDNESS, smallEffectDuration),

            SuspiciousEffectEntry.create(PotionEffectType.RESISTANCE, smallEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.ABSORPTION, smallEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.FIRE_RESISTANCE, bigEffectDuration),
            SuspiciousEffectEntry.create(PotionEffectType.SPEED, smallEffectDuration)
    );
    public VerySuspiciousStew(Enderwolf wolf, int cooldown)
    {
        super(wolf, cooldown);
    }

    @EventHandler
    public void exhaustEvent(EntityExhaustionEvent event)
    {
        if(!isReady())
            return;
        Wolf eWolf = enderwolf.getWolf();
        if(event.getEntity() instanceof Player p && p.getFoodLevel() <= 10 && p.getWorld() == eWolf.getWorld())
        {
            enderwolf.tempOwner(p);
            spawnStew(p);
            enterCooldown();
        }
    }

    private void spawnStew(Player p)
    {
        ItemStack stew = ItemStack.of(Material.SUSPICIOUS_STEW);
        SuspiciousStewMeta potions = (SuspiciousStewMeta) stew.getItemMeta();
        potions.itemName(Component.text("Very Suspicious stew"));
        potions.lore(List.of(Component.text("Where did it even find this?")));
        int primary = SavingPrivateRahya.RAND.nextInt(effects.size());
        int secondary = SavingPrivateRahya.RAND.nextInt(effects.size());
        if(primary == secondary)
        {
            SuspiciousEffectEntry perma = SuspiciousEffectEntry.create(effects.get(primary).effect(), PotionEffect.INFINITE_DURATION);
            potions.addCustomEffect(perma, true);
            potions.setRarity(ItemRarity.RARE);
            potions.lore(List.of(Component.text("This is NOT your grandmother's stew!")));
        }
        else
        {
            potions.addCustomEffect(effects.get(primary), true);
            potions.addCustomEffect(effects.get(secondary), true);
        }
        stew.setItemMeta(potions);
        var remaining = p.getInventory().addItem(stew);
        if (!remaining.isEmpty()) {
            p.getWorld().dropItemNaturally(p.getLocation(), stew);
        }
    }
}
