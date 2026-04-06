package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayersConnect implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(!game.getParticipants().containsKey(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            if(game.isPreGame())
            {
                game.addParticipant(event.getPlayer());
                event.getPlayer().teleportAsync(game.gameCenter);
            }
            else
            {
                event.getPlayer().sendMessage("The game has already started, and you will not be able to participate, sorry!");
            }
        }
        game.getTime().onPlayerConnect(event.getPlayer());
    }
}
