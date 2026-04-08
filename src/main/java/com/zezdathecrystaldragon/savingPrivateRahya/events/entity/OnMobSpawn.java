package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnMobSpawn implements Listener
{
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event)
    {

        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(event.getEntity() instanceof LivingEntity && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            game.getMobs().handleRandomSpawns(event);
        }
    }
    @EventHandler
    public void onEntitySpawnFromEgg(CreatureSpawnEvent event)
    {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            return;
        if(event.getEntity() instanceof Snowman sm)
        {
            sm.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(20*600, 0));
            sm.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(128D);
            sm.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40D);
            sm.setHealth(sm.getAttribute(Attribute.MAX_HEALTH).getValue());
            sm.getPersistentDataContainer().set(SavingPrivateRahya.PLUGIN.VIP_MOB, PersistentDataType.BOOLEAN, true);
        }
        if(event.getEntity() instanceof IronGolem gm)
        {
            gm.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(32D);
            gm.getAttribute(Attribute.ARMOR).setBaseValue(16D);
            gm.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(16D);
            gm.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, 0.33D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            gm.getAttribute(Attribute.ATTACK_DAMAGE).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, 0.33D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            gm.getAttribute(Attribute.ATTACK_KNOCKBACK).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, 1D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            gm.getAttribute(Attribute.SCALE).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, -0.50D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            gm.getPersistentDataContainer().set(SavingPrivateRahya.PLUGIN.VIP_MOB, PersistentDataType.BOOLEAN, true);

        }
    }

}
