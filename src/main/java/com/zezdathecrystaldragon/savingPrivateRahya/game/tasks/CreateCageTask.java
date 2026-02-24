package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.WorldModifier;
import com.zezdathecrystaldragon.savingPrivateRahya.tasks.CancellableRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.logging.Logger;

public class CreateCageTask extends CancellableRunnable {

    private final Game game;
    private final Logger logger;
    private final long startTime;
    private WorldModifier wm;

    private long lastWarningTime;
    private int totalAttempts = 0;
    private boolean found = false;

    public CreateCageTask(Game game, WorldModifier worldModifier) {
        this.game = game;
        this.logger = SavingPrivateRahya.PLUGIN.getLogger();
        this.startTime = System.currentTimeMillis();
        this.wm = worldModifier;
        this.lastWarningTime = startTime;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            totalAttempts++;

            Location loc = wm.findOneRandomLocation(game.nether);

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
}