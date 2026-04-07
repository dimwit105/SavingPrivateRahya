package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.MobManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.HeatSeekingFireball;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class OnProjectileLaunch implements Listener
{
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event)
    {
        if(event.getEntity().getShooter() instanceof Snowman sm && event.getEntity().getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
        {
            if(!sm.getPersistentDataContainer().has(SavingPrivateRahya.PLUGIN.VIP_MOB))
                return;
            event.setCancelled(true);
            Projectile snowball = event.getEntity();
            Fireball fireball = sm.getWorld().createEntity(event.getEntity().getLocation(), Fireball.class);
            fireball.setShooter(sm);
            fireball.setYield(2F);
            fireball.setIsIncendiary(true);
            fireball.setVelocity(new Vector(0,0.1,0));
            fireball.spawnAt(snowball.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

            SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(fireball, new HeatSeekingFireball(fireball, sm.getTarget(), 0.5F), 6, 1);
        }
        if(event.getEntity().getShooter() instanceof Mob le && le.getPersistentDataContainer().has(MobManager.TRACKING))
        {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(0.33F));
            SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(event.getEntity(), new HeatSeekingFireball(event.getEntity(), le.getTarget(), 0.5F), 8, 1);
        }
    }
}
