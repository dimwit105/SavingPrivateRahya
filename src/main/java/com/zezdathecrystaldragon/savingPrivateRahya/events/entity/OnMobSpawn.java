package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class OnMobSpawn implements Listener
{
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        game.getMobs().handleRandomSpawns(event);
    }
}
