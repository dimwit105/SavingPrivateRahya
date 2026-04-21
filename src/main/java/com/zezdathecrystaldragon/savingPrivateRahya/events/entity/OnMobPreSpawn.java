package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Salmon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

public class OnMobPreSpawn implements Listener
{
    @EventHandler
    public void onMobPreSpawn(PreCreatureSpawnEvent event) {
        if (event.getReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

        Location loc = event.getSpawnLocation();
        if(loc.getWorld().getEnvironment() == World.Environment.NETHER && loc.getBlockY() >= 128)
            return;
        if(event.getType() == EntityType.GHAST)
            return;
        if (loc.getBlock().getBiome() == Biome.SOUL_SAND_VALLEY) {


            if (loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() &&
                    loc.getBlock().getType().isAir()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                var ent = loc.getWorld().createEntity(loc, event.getType().getEntityClass());
                if(ent instanceof AbstractSkeleton as)
                    as.getEquipment().setItemInMainHand(ItemStack.of(Material.BOW));
                if(ent instanceof Mob m && isSpaceSafe(loc, m))
                    ent.spawnAt(loc, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        }
    }
    private boolean isSpaceSafe(Location loc, Mob mob) {
        double height = mob.getHeight();
        double width = mob.getWidth();

        BoundingBox box = BoundingBox.of(loc, width / 2.0, height, width / 2.0);

        World world = loc.getWorld();

        for (int x = (int) box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = (int) box.getMinY(); y <= box.getMinY(); y++) {
                for (int z = (int) box.getMinZ(); z <= box.getMaxZ(); z++) {
                    if (world.getBlockAt(x, y, z).getType().isSolid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
