package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnPlayerDamaged implements Listener
{
    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(event.getEntity() instanceof Player p)
        {
            if(game.getVip() != null && game.getVip().getPlayer() == p && event.getFinalDamage() > 0)
            {
                game.getVip().getShield().takeHit();
            }
        }
    }
}
