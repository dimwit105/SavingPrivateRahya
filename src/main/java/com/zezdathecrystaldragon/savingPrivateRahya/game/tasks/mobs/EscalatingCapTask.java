package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.MobManager;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;

public class EscalatingCapTask extends CancellableRunnable
{
    private MobManager mm;

    public EscalatingCapTask(MobManager mm)
    {
        this.mm = mm;
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(this, 20, 20);
    }

    @Override
    public void run()
    {
        mm.escalateNaturalSpawns();
    }
}
