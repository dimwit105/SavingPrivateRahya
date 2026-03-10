package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.concurrent.CompletableFuture;

public abstract class WorldTask extends CancellableRunnable
{
    protected final WorldModifier wm;
    protected final Game game;

    WorldTask(Game game, WorldModifier wm)
    {
        this.game = game;
        this.wm = wm;
    }

    protected CompletableFuture<Void> loadLocalChunks(World world, int x, int z)
    {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        CompletableFuture<?>[] grid = new CompletableFuture[9];
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                grid[i++] = world.getChunkAtAsync(chunkX + dx, chunkZ + dz);
            }
        }
        return CompletableFuture.allOf(grid);
    }

    protected boolean isCubeClear(World w, Location loc, int size)
    {
        return isCubeClear(w, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), size);
    }

    protected boolean isCubeClear(World w, int x0, int y0, int z0, int size) {
        return isRectCuboidClear(w, x0, y0, z0, size, size, size);
    }

    protected boolean isRectCuboidClear(World w, int x0, int y0, int z0, int sizeX, int sizeY, int sizeZ) {
        for (int x = x0; x < x0 + sizeX; x++) {
            for (int y = y0; y < y0 + sizeY; y++) {
                for (int z = z0; z < z0 + sizeZ; z++) {
                    Block block = w.getBlockAt(x, y, z);
                    Material type = block.getType();

                    if (type.isAir()) continue;
                    if (wm.nonBlockers.contains(type)) {
                        continue;
                    }
                    if (!type.isSolid()) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
