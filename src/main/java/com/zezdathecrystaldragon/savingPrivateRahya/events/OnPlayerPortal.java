package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.VeryImportantParticipant;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OnPlayerPortal implements Listener
{
    Game game = SavingPrivateRahya.GAME;
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        Map<UUID, Participant> parts = game.getParticipants();
        Participant part = parts.get(event.getPlayer().getUniqueId());
        if(part == null)
            return;
        if(part instanceof VeryImportantParticipant vip)
        {
            if(event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL)
            {
                game.endGame(GameEndReason.VICTORY);
            }
        }
    }
}
