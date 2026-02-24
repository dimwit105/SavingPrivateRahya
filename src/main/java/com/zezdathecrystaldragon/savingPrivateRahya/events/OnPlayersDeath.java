package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class OnPlayersDeath implements Listener
{
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Game game = SavingPrivateRahya.GAME;
        UUID id = event.getPlayer().getUniqueId();

        if(game.getParticipants().containsKey(id))
            game.getParticipants().get(id).onDeath(event);
    }
}
