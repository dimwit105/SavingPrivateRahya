package com.zezdathecrystaldragon.savingPrivateRahya.players.vip;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.SpawnLocation;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.VIPTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.aura.VIPEffectAura;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.shield.RegeneratingShieldTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class VeryImportantParticipant extends Participant
{
    private final VIPEffectAura aura;
    private final RegeneratingShieldTask shield;
    public VeryImportantParticipant(Participant p)
    {
        super(p);
        this.aura = new VIPEffectAura(this);
        this.shield = new RegeneratingShieldTask(this);
        this.spawnLocation = SpawnLocation.NETHER;
    }

    /**
     * This will only succeed if you specify Nether, but their Spawn Location is already Nether, so there is little point in reaffirming it.
     * @param location Either Nether for VIP spawning, or Overworld for regular spawning.
     * @return Whether setting was successful. VIPs can only spawn in the nether, and can not have an overworld spawn.
     */
    @Override
    public boolean electSpawn(SpawnLocation location)
    {
        if(location == SpawnLocation.NETHER)
            return super.electSpawn(location);
        return false;
    }

    @Override
    protected void giveStartingGear(SpawnLocation location)
    {
        super.giveStartingGear(location);
        getPlayer().getInventory().addItem(ItemStack.of(Material.IRON_GOLEM_SPAWN_EGG, 3));

        NamespacedKey swordBuff = new NamespacedKey(SavingPrivateRahya.PLUGIN, "vipswordbuff");
        ItemStack vipSword = new ItemStack(Material.GOLDEN_SWORD);
        Damageable swordMeta = (Damageable) vipSword.getItemMeta();
        swordMeta.setMaxDamage(12);
        swordMeta.setRarity(ItemRarity.EPIC);
        swordMeta.customName(Component.text(getPlayer().getName() + "'s last resort"));
        //swordMeta.addAttributeModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(swordBuff, 12D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        swordMeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
        vipSword.setItemMeta(swordMeta);
        getPlayer().getInventory().addItem(vipSword);
    }
    @Override
    public void onDeath(PlayerDeathEvent event)
    {
        eliminate();
        game.endGame(GameEndReason.VIP_DIED);
    }

    @Override
    public VeryImportantParticipant toVIP() {return this;}

    public Participant unVIP()
    {
        for(ParticipantTask task : tasks)
        {
            if(task instanceof VIPTask vipTask)
            {
                vipTask.onDemote();
            }
        }
        return new Participant(this);
    }

    public VIPEffectAura getAura() {return aura;}
    public RegeneratingShieldTask getShield() {return shield;}
}
