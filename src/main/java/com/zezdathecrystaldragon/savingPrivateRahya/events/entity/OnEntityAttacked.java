package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.HeatSeekingFireball;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnEntityAttacked implements Listener
{
    @EventHandler
    public void onEntityAttacked(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        if(damager instanceof IronGolem ig && ig.getPersistentDataContainer().has(SavingPrivateRahya.PLUGIN.VIP_MOB))
        {
            if(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                return;
            event.getEntity().getWorld().createExplosion(ig, event.getEntity().getLocation(), 4F, false, false);
        }
        if(HeatSeekingFireball.heatSeekers.containsKey(event.getEntity()))
        {
            HeatSeekingFireball.heatSeekers.get(event.getEntity()).stun();
        }
    }
}
