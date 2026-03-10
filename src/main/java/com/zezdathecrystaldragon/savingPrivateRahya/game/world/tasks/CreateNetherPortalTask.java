package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
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

        Location nCenter = GameMath.netherify(game.nether, game.gameCenter);
        int centerX = nCenter.getBlockX();
        int centerZ = nCenter.getBlockZ();

        loadLocalChunks(nether, centerX, centerZ).thenAccept(v -> {
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                for (int attempts = 0; attempts < 1000; attempts++) {

                    int x = centerX + random.nextInt(32) - 16;
                    int z = centerZ + random.nextInt(32) - 16;

                    int y = 32 + random.nextInt(60);
                    Block floor = nether.getBlockAt(x, y, z);

                    if (floor.getType().isSolid() && !floor.isLiquid()) {
                        int spawnY = floor.getY() + 1;

                        int widthX = (orientation == WorldModifier.PortalOrientation.X_AXIS) ? 4 : 1;
                        int widthZ = (orientation == WorldModifier.PortalOrientation.Z_AXIS) ? 4 : 1;

                        // 3. Check if the cuboid is clear for the portal structure
                        if (isRectCuboidClear(nether, x, spawnY, z, widthX, 5, widthZ)) {
                            finalResult.complete(new Location(nether, x, spawnY, z));
                            return;
                        }
                    }
                }

                finalResult.complete(new Location(nether, centerX, 64, centerZ));
            });
        });
        return finalResult;
    }
}