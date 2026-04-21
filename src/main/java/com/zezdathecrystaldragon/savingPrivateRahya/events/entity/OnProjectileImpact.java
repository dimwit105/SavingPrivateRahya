package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.HeatSeekingFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class OnProjectileImpact implements Listener
{
    @EventHandler
    public void OnProjectileHit(ProjectileHitEvent event)
    {
        if(!HeatSeekingFireball.heatSeekers.containsKey(event.getEntity()))
            return;
        if(event.getHitEntity() instanceof Ghast ghast && event.getEntity().getShooter() instanceof Entity e)
        {
            ghast.damage(5, e);
        }
    }
}
