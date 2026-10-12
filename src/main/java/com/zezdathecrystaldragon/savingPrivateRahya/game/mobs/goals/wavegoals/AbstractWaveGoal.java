package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import org.bukkit.entity.Mob;

public abstract class AbstractWaveGoal implements Goal<Mob>
{
    protected final AssaultManager manager;
    protected final Mob assaultMob;
    public AbstractWaveGoal(AssaultManager manager, Mob mob)
    {
        this.manager = manager;
        this.assaultMob = mob;
    }
}
