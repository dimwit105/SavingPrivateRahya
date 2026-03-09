package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CreateNetherPortalTask extends WorldTask {

    public CreateNetherPortalTask(Game game, WorldModifier wm) {
        super(game, wm);
    }

    @Override
    public void run() {
        World nether = game.nether;
        WorldModifier.PortalOrientation orientation = WorldModifier.PortalOrientation.X_AXIS;

        findNetherLocation(nether, orientation).thenAccept(loc -> {
            if (loc == null) return;

            SavingPrivateRahya.runNextTick(task -> {
                boolean isFallback = (loc.getBlockX() == 0 && loc.getBlockY() == 64 && loc.getBlockZ() == 0);

                wm.buildPortalWithPlatform(nether, loc, orientation, isFallback);
            });
        });
    }

    public CompletableFuture<Location> findNetherLocation(World nether, WorldModifier.PortalOrientation orientation) {
        CompletableFuture<Location> finalResult = new CompletableFuture<>();
        Random random = SavingPrivateRahya.RAND;

        // Load center chunk (0,0) and immediate neighbors (3x3 grid)
        CompletableFuture<?>[] grid = new CompletableFuture[9];
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                grid[count++] = nether.getChunkAtAsync(dx, dz);
            }
        }

        CompletableFuture.allOf(grid).thenAccept(v -> {
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                for (int attempts = 0; attempts < 1000; attempts++) {
                    // Stay within 16 blocks of 0,0
                    int x = random.nextInt(32) - 16;
                    int z = random.nextInt(32) - 16;

                    // In the Nether, we search for a "pocket" rather than the highest block
                    int y = 32 + random.nextInt(60);
                    Block floor = nether.getBlockAt(x, y, z);

                    if (floor.getType().isSolid() && !floor.isLiquid()) {
                        y = floor.getY() + 1;

                        int widthX = (orientation == WorldModifier.PortalOrientation.X_AXIS) ? 4 : 1;
                        int widthZ = (orientation == WorldModifier.PortalOrientation.Z_AXIS) ? 4 : 1;

                        if (isRectCuboidClear(nether, x, y, z, widthX, 5, widthZ)) {
                            finalResult.complete(new Location(nether, x, y, z));
                            return;
                        }
                    }
                }
                // Fallback: Force it at 0, 64, 0 if search fails
                finalResult.complete(new Location(nether, 0, 64, 0));
            });
        });
        return finalResult;
    }
}