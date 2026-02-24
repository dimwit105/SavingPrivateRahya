package com.zezdathecrystaldragon.savingPrivateRahya.players;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class VeryImportantParticipant extends Participant
{
    VeryImportantParticipant(Participant p)
    {
        super(p);
        this.spawnLocation = SpawnLocation.NETHER;
    }

    /**
     * This will only succeed if you specify Nether, but their Spawn Location is already Nether, so there is little point in reaffirming it.
     * @param location Either Nether for VIP spawning, or Overworld for regular spawning.
     * @return Whether setting was successful. VIPs can only spawn in the nether, and can not have an overworld spawn.
     */
    public boolean electSpawn(SpawnLocation location)
    {
        if(location == SpawnLocation.NETHER)
            return super.electSpawn(location);
        return false;
    }

    public void beginGame()
    {
        super.beginGame();

        getPlayer().getInventory().addItem(ItemStack.of(Material.IRON_GOLEM_SPAWN_EGG, 3));

        NamespacedKey swordBuff = new NamespacedKey(SavingPrivateRahya.PLUGIN, "vipswordbuff");
        ItemStack vipSword = new ItemStack(Material.GOLDEN_SWORD);
        Damageable swordMeta = (Damageable) vipSword.getItemMeta();
        swordMeta.setMaxDamage(12);
        swordMeta.setRarity(ItemRarity.EPIC);
        swordMeta.customName(Component.text(getPlayer().getName() + "'s last resort"));
        swordMeta.addAttributeModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(swordBuff, 1D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        vipSword.setItemMeta(swordMeta);
        getPlayer().getInventory().addItem(vipSword);
    }

    public void onDeath(PlayerDeathEvent event)
    {
        eliminate();
        game.endGame(GameEndReason.VIP_DIED);
    }

    @Override
    public VeryImportantParticipant toVIP() {return this;}

    public Participant unVIP()
    {
        return new Participant(this);
    }
}
