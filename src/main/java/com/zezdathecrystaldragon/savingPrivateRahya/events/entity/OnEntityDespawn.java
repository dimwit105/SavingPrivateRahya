package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

public class OnEntityDespawn implements Listener
{
    @EventHandler
    public void onEntityDespawn(EntityRemoveFromWorldEvent event)
    {
        Entity removed = event.getEntity();
        if(removed instanceof Mob mob && mob.getPersistentDataContainer().has(AssaultManager.ASSAULT_MOB) && mob.getPersistentDataContainer().get(AssaultManager.ASSAULT_MOB, PersistentDataType.BOOLEAN))
        {
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, removed.getUniqueId() + " called despawn event");
            SavingPrivateRahya.PLUGIN.getGame().getAssaultManager().assaultMobRemoved();
        }
    }
}
