package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.List;

/**
 * This event Listener watches for new portals, and blows up anything that isn't close to the origin of the world.
 * Gives 64 blocks of leniency around 0,0.
 */
public class OnPortalCreated implements Listener
{
    int check = SavingPrivateRahya.PLUGIN.getGame().extractionZoneTotal;
    @EventHandler
    public void onPortalCreated(PortalCreateEvent event) {
        if (event.getReason() != PortalCreateEvent.CreateReason.FIRE) return;

        List<BlockState> blocks = event.getBlocks();
        boolean outOfBounds = false;

        for (BlockState block : blocks) {
            int x = block.getX();
            int z = block.getZ();

            if (Math.abs(x) > check || Math.abs(z) > check) {
                outOfBounds = true;
                break;
            }
        }

        if (outOfBounds) {
            event.setCancelled(true);

            for (BlockState block : blocks) {
                if(block.getType().equals(Material.OBSIDIAN))
                {
                    block.setType(Material.CRYING_OBSIDIAN);
                    block.update(true);
                }
                if (!SavingPrivateRahya.PLUGIN.getGame().isPreGame()) {
                    block.getWorld().createExplosion(block.getLocation(), 3f, true, true);
                }
            }
        }
    }
}
