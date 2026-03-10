package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    @EventHandler
    public void onPortalCreated(PortalCreateEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        Location overworldCenter = game.gameCenter;
        Location netherCenter = GameMath.netherify(game.nether, game.gameCenter);

        if (event.getReason() != PortalCreateEvent.CreateReason.FIRE) return;
        boolean isOverworld = event.getWorld().getEnvironment() == World.Environment.NORMAL;
        Location center = isOverworld ? overworldCenter : netherCenter;
        int buffer = game.extractionZoneBuffer;
        int check = isOverworld ? game.extractionZone + buffer : (game.extractionZone >> 3) + buffer;

        List<BlockState> blocks = event.getBlocks();
        boolean outOfBounds = false;

        for (BlockState block : blocks) {
            int x = block.getX();
            int z = block.getZ();

            if (Math.abs(x - center.getBlockX()) > check || Math.abs(z - center.getBlockZ()) > check) {
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
