package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.NaturalMobManager;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;

public class EscalatingCapTask extends CancellableRunnable
{
    private NaturalMobManager mm;

    public EscalatingCapTask(NaturalMobManager mm)
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
