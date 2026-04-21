package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

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
                wm.buildPortalWithPlatform(overworld, loc, orientation, false);
                var beacon = wm.buildBeaconPyramid(loc.clone().add(0,5,0), 1);
                beacon.setPrimaryEffect(PotionEffectType.HASTE);
                beacon.setEffectRange(256);
                beacon.update();
            });
        });
    }

    public CompletableFuture<Location> findOneRandomLocation(World overworld, WorldModifier.PortalOrientation orientation) {
        CompletableFuture<Location> finalResult = new CompletableFuture<>();
        Random random = SavingPrivateRahya.RAND;

        int centerX = game.gameCenter.getBlockX();
        int centerZ = game.gameCenter.getBlockZ();

        loadLocalChunks(overworld, centerX, centerZ).thenAccept(v -> {
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                for (int attempts = 0; attempts < 600; attempts++) {
                    int x = centerX + random.nextInt(128) - 64;
                    int z = centerZ + random.nextInt(128) - 64;

                    Block floor = overworld.getHighestBlockAt(x, z);
                    if (floor.getType().isSolid() && !floor.isLiquid()) {
                        if (isRectCuboidClear(overworld, x, floor.getY() + 1, z,
                                (orientation == WorldModifier.PortalOrientation.X_AXIS ? 4 : 1), 5,
                                (orientation == WorldModifier.PortalOrientation.Z_AXIS ? 4 : 1))) {
                            finalResult.complete(new Location(overworld, x, floor.getY() + 1, z));
                            return;
                        }
                    }
                }
                finalResult.complete(new Location(overworld, centerX, 64, centerZ));
            });
        });
        return finalResult;
    }
}
