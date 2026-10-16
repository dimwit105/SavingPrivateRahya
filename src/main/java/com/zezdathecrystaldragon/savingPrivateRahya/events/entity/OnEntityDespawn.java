package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.persistence.PersistentDataType;

public class OnEntityDespawn implements Listener
{
    @EventHandler
    public void onEntityDespawn(EntityRemoveEvent event)
    {
        Entity removed = event.getEntity();
        if(removed instanceof Mob mob &&
                (event.getCause() == EntityRemoveEvent.Cause.DESPAWN
                        || event.getCause() == EntityRemoveEvent.Cause.UNLOAD))
        {
            if(mob.getPersistentDataContainer().has(AssaultManager.ASSAULT_MOB) && mob.getPersistentDataContainer().get(AssaultManager.ASSAULT_MOB, PersistentDataType.BOOLEAN))
            {
                SavingPrivateRahya.PLUGIN.getGame().getAssaultManager().assaultMobDespawned();
            }
        }
    }
}
