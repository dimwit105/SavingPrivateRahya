package com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * This creates the nether cage for the VIP to spawn in. Designed to be run repeatedly until a suitable spot is found.
 * This does not fallback, and will literally keep searching until a spot is found indefinitely.
 */
public class CreateCageTask extends WorldTask {

    private final int BATCHSIZE = 5;

    private final Logger logger;
    private final long startTime;
    private final int cageSize;
    private boolean searching = false;

    private long lastWarningTime;
    private int totalAttempts = 0;

    public CreateCageTask(Game game, WorldModifier worldModifier, int cageSize) {
        super(game, worldModifier);
        this.logger = SavingPrivateRahya.PLUGIN.getLogger();
        this.startTime = System.currentTimeMillis();
        this.cageSize = cageSize;
        this.lastWarningTime = startTime;
    }

    @Override
    public void run() {
        if(searching)
        {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - startTime) / 1000;

            if (elapsedSeconds >= 60 && (currentTime - lastWarningTime) >= 5000) {
                logger.warning("Still searching for VIP Cage... Elapsed: "
                        + elapsedSeconds + "s | Total Attempts: " + totalAttempts);
                lastWarningTime = currentTime;
            }
            return;
        }
        searching = true;

        List<CompletableFuture<Location>> batch = new ArrayList<>();
        for(int i = 0; i < BATCHSIZE; i++)
        {
            batch.add(findOneRandomLocation(game.nether));
        }
        CompletableFuture.allOf(batch.toArray(new CompletableFuture[0])).thenAccept(v -> {
            Location winner = null;
            for(CompletableFuture<Location> future : batch)
            {
                Location loc = future.join();
                totalAttempts++;
                if(loc != null)
                {
                    winner = loc;
                    break;
                }
            }
            final Location finalWinner = winner;
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                if(finalWinner != null)
                {
                    wm.createVIPCage(game.nether, finalWinner);
                    this.cancel();
                }
                else
                {
                    searching = false;
                }
            });
        });
    }
    public CompletableFuture<Location> findOneRandomLocation(World nether) {

        CompletableFuture<Location> finalResult = new CompletableFuture<>();

        Random random = SavingPrivateRahya.RAND;
        double theta = random.nextDouble() * 2 * Math.PI;
        double skew = random.nextDouble();
        double r = game.vipDistance + (skew * (game.vipDistance * 0.1666D));

        int x = (int) (r * Math.cos(theta));
        int z = (int) (r * Math.sin(theta));

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        CompletableFuture<?>[] grid = new CompletableFuture[9];
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                grid[i++] = game.nether.getChunkAtAsync(chunkX + dx, chunkZ + dz);
            }
        }
        CompletableFuture.allOf(grid).thenAccept(v -> {
            SavingPrivateRahya.runNextTick(wrappedTask -> {
                Location found = null;
                for (int y = 30; y < 110; y++) {
                    Block floor = nether.getBlockAt(x, y, z);

                    if (floor.getType().isSolid() && floor.getType() != Material.LAVA) {
                        if (isCubeClear(nether, x, y + 1, z, cageSize)) {
                            if (nether.getBlockAt(x, y - 1, z).getType() != Material.LAVA) {
                                found = new Location(nether, x, y, z);
                                break;
                            }
                        }
                    }
                }
                finalResult.complete(found);
            });
        });
        return finalResult;
    }
}