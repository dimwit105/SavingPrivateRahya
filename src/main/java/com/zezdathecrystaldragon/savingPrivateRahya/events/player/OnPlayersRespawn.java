package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class OnPlayersRespawn implements Listener
{
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        UUID id = event.getPlayer().getUniqueId();

        if(game.getParticipants().containsKey(id))
            game.getParticipants().get(id).onRespawn(event);
    }
}
