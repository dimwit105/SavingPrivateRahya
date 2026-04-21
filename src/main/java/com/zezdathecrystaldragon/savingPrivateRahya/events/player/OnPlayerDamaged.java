package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class OnPlayerDamaged implements Listener
{
    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(event.getEntity() instanceof Player p)
        {
            SavingPrivateRahya.PLUGIN.getGame().getVip()
                    .filter(vip -> vip.getPlayer().filter(p::equals).isPresent())
                    .ifPresent(vip -> {
                        if (event.getFinalDamage() > 0 || event.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION) < 0) {
                            vip.getShield().takeHit();
                        }
                    });
        }
    }
}
