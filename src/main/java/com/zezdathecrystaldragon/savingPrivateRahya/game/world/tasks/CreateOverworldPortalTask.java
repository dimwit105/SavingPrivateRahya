package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CreateOverworldPortalTask extends WorldTask {

    public CreateOverworldPortalTask(Game game, WorldModifier wm) {
        super(game, wm);
    }

    @Override
    public void run() {
        World overworld = game.overworld;

        WorldModifier.PortalOrientation orientation = SavingPrivateRahya.RAND.nextBoolean() ?
                WorldModifier.PortalOrientation.X_AXIS : WorldModifier.PortalOrientation.Z_AXIS;

        findOneRandomLocation(overworld, orientation).thenAccept(loc -> {
            if (loc == null) return;

            SavingPrivateRahya.runNextTick(task -> {
                boolean isFallback = (loc.getBlockX() == 0 && loc.getBlockY() == 64 && loc.getBlockZ() == 0);
                wm.buildPortalWithPlatform(overworld, loc, orientation, isFallback);
            });
        });
    }

    public CompletableFuture<Location> findOneRandomLocation(World overworld, WorldModifier.PortalOrientation orientation) {
        CompletableFuture<Location> finalResult = new CompletableFuture<>();
        Random random = SavingPrivateRahya.RAND;

        CompletableFuture<?>[] grid = new CompletableFuture[64];
        int count = 0;
        for (int dx = -4; dx <= 3; dx++) {
            for (int dz = -4; dz <= 3; dz++) {
                grid[count++] = overworld.getChunkAtAsync(dx, dz);
            }
        }

        CompletableFuture.allOf(grid).thenAccept(v -> {
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                for (int attempts = 0; attempts < 600; attempts++) {
                    int x = random.nextInt(128) - 64;
                    int z = random.nextInt(128) - 64;

                    Block floor = overworld.getHighestBlockAt(x, z);

                    if (floor.getType().isSolid() && !floor.isLiquid()) {
                        int y = floor.getY() + 1;

                        int widthX = (orientation == WorldModifier.PortalOrientation.X_AXIS) ? 4 : 1;
                        int widthZ = (orientation == WorldModifier.PortalOrientation.Z_AXIS) ? 4 : 1;

                        if (isRectCuboidClear(overworld, x, y, z, widthX, 5, widthZ)) {
                            finalResult.complete(new Location(overworld, x, y, z));
                            return;
                        }
                    }
                }
                finalResult.complete(new Location(overworld, 0, 64, 0));
            });
        });
        return finalResult;
    }
}
