package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class OnPlayersDisconnect implements Listener
{
    Game game = SavingPrivateRahya.GAME;
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {
        UUID id = event.getPlayer().getUniqueId();
        if(game.getParticipants().containsKey(id))
            game.getParticipants().get(id).onDisconnect(event);
        if(game.isPreGame())
            game.removeParticipant(id);
    }
}
