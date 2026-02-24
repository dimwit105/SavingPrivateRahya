package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.WorldModifier;
import com.zezdathecrystaldragon.savingPrivateRahya.tasks.CancellableRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;
import java.util.logging.Logger;

public class CreateCageTask extends CancellableRunnable {

    private final Game game;
    private final Logger logger;
    private final long startTime;
    private WorldModifier wm;
    private final int cageSize;

    private long lastWarningTime;
    private int totalAttempts = 0;
    private boolean found = false;

    public CreateCageTask(Game game, WorldModifier worldModifier, int cageSize) {
        this.game = game;
        this.logger = SavingPrivateRahya.PLUGIN.getLogger();
        this.startTime = System.currentTimeMillis();
        this.wm = worldModifier;
        this.cageSize = cageSize;
        this.lastWarningTime = startTime;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            totalAttempts++;

            Location loc = findOneRandomLocation(game.nether);

            if (loc != null) {
                wm.createVIPCage(game.nether, loc);

                long duration = (System.currentTimeMillis() - startTime) / 1000;
                logger.info("Success! Cage created at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                        + " after " + duration + "s and " + totalAttempts + " attempts.");

                found = true;
                this.cancel();
                break;
            }
        }

        // Warning Logic
        if (!found) {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - startTime) / 1000;

            if (elapsedSeconds >= 60 && (currentTime - lastWarningTime) >= 5000) {
                logger.warning("Still searching for VIP Cage... Elapsed: "
                        + elapsedSeconds + "s | Total Attempts: " + totalAttempts);
                lastWarningTime = currentTime;
            }
        }
    }
    public Location findOneRandomLocation(World nether) {
        Random random = SavingPrivateRahya.RAND;
        double theta = random.nextDouble() * 2 * Math.PI;
        double skew = random.nextDouble();
        double r = 1500 + (skew * 200);

        int x = (int) (r * Math.cos(theta));
        int z = (int) (r * Math.sin(theta));

        for (int y = 30; y < 110; y++) {
            Block floor = nether.getBlockAt(x, y, z);

            if (floor.getType().isSolid() && floor.getType() != Material.LAVA) {
                if (isAreaClear(nether, x, y + 1, z, cageSize)) {
                    if (nether.getBlockAt(x, y - 1, z).getType() != Material.LAVA) {
                        return new Location(nether, x, y, z);
                    }
                }
            }
        }
        return null;
    }
    private boolean isAreaClear(World w, int x0, int y0, int z0, int size) {
        for (int x = x0; x < x0 + size; x++) {
            // We check the 4 layers above the floor (y0 to y0 + size - 2)
            for (int y = y0; y < y0 + size - 1; y++) {
                for (int z = z0; z < z0 + size; z++) {
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