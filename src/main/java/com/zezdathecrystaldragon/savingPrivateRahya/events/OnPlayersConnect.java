package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayersConnect implements Listener
{
    Game game = SavingPrivateRahya.PLUGIN.getGame();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if(!game.getParticipants().containsKey(event.getPlayer().getUniqueId()))
        {
            if(game.isPreGame())
            {
                game.addParticipant(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            else
            {
                event.getPlayer().kick(Component.text("Game is already in progress, sorry!"));
            }
        }

        game.getTime().onPlayerConnect(event.getPlayer());
    }
}
