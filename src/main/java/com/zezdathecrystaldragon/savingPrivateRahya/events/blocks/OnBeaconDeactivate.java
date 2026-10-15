package com.zezdathecrystaldragon.savingPrivateRahya.events.blocks;

import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnBeaconDeactivate implements Listener
{
    @EventHandler
    public void onBeaconDeactivate(BeaconDeactivatedEvent event)
    {
        Beacon beacon = event.getBeacon();
        if(beacon == null)
            return;
        if(beacon.getPersistentDataContainer().has(WorldModifier.SPAWN_BEACON))
        {
            beacon.getPersistentDataContainer().remove(WorldModifier.SPAWN_BEACON);
        }
    }
}
