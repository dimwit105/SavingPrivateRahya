package com.zezdathecrystaldragon.savingPrivateRahya.events.blocks;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnReceiveBeaconEffect implements Listener
{
    @EventHandler
    public void onBeaconEffect(BeaconEffectEvent event)
    {
        Block block = event.getBlock();
        int duration = event.getEffect().getDuration();
        int amp = event.getEffect().getAmplifier();
        if(block.getState() instanceof Beacon beacon && beacon.getPersistentDataContainer().has(WorldModifier.SPAWN_BEACON) && beacon.getPersistentDataContainer().get(WorldModifier.SPAWN_BEACON, PersistentDataType.BOOLEAN))
        {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1, amp));
        }
    }
}
