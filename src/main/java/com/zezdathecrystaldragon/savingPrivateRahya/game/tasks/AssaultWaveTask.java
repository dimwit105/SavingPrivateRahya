package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;

public class AssaultWaveTask extends CancellableRunnable
{
    private AssaultManager manager;
    public AssaultWaveTask(AssaultManager manager)
    {
        this.manager = manager;
    }
    @Override
    public void run()
    {
        manager.tick();
    }
}
