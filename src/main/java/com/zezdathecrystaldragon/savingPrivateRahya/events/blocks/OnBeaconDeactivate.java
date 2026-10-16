package com.zezdathecrystaldragon.savingPrivateRahya.events.blocks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

public class OnBeaconDeactivate implements Listener
{
    @EventHandler
    public void onBeaconDeactivate(BeaconDeactivatedEvent event)
    {
        try
        {
            Beacon beacon = event.getBeacon();
            if(beacon == null)
                return;
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Beacon deactivate fired! Beacon block: " + beacon.getLocation().toString());
            if(beacon.getTier() == 0)
                SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Beacon is now tier 0!");
            if(beacon.getPersistentDataContainer().has(WorldModifier.SPAWN_BEACON))
            {
                beacon.getPersistentDataContainer().set(WorldModifier.SPAWN_BEACON, PersistentDataType.BOOLEAN, false);
                beacon.update();
            }
        }
        catch (IllegalStateException e)
        {
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Becaon deactivated with an illegal state exception! - This is a paper bug : https://github.com/PaperMC/Paper/issues/8947");
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, e.getMessage());
        }

    }
}
