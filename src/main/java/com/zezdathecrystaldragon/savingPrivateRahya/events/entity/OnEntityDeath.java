package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnEntityDeath implements Listener
{
    @EventHandler
    public void onEntityKilled(EntityDeathEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(event.getEntity() instanceof Wolf wolf && wolf.getPersistentDataContainer().has(SavingPrivateRahya.PLUGIN.VIP_MOB)
                && game.getVip() != null
                && game.getVip().getEnderwolf() != null)
        {
            game.getVip().getEnderwolf().wolfDied(event);
        }
    }
}
