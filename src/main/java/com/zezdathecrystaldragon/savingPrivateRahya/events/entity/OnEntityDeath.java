package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Level;

public class OnEntityDeath implements Listener
{
    @EventHandler
    public void onEntityKilled(EntityDeathEvent event)
    {
        SavingPrivateRahya.PLUGIN.getGame().getVip()
                .filter(vip -> event.getEntity() instanceof Wolf wolf
                        && wolf.getPersistentDataContainer().has(SavingPrivateRahya.PLUGIN.VIP_MOB))
                .flatMap(VeryImportantParticipant::getEnderwolf)
                .ifPresent(enderwolf -> enderwolf.wolfDied(event));
    }

    @EventHandler
    public void onAssaultMobKilled(EntityDeathEvent event)
    {
        var entity = event.getEntity();
        if(entity.getPersistentDataContainer().has(AssaultManager.ASSAULT_MOB))
        {
            if(entity.getLastDamageCause() != null && entity.getLastDamageCause().getDamageSource().getCausingEntity() != null)
            {
                Game game = SavingPrivateRahya.PLUGIN.getGame();
                game.getAssaultManager().assaultMobKilled();
            }
        }
    }
}
