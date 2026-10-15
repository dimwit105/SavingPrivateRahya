package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;

public abstract class AbstractWaveGoal implements Goal<Mob>
{
    protected final AssaultManager manager;
    protected final Mob assaultMob;
    protected final GoalKey<Mob> key;
    public AbstractWaveGoal(AssaultManager manager, Mob mob, String key)
    {
        this.manager = manager;
        this.assaultMob = mob;
        this.key = GoalKey.of(Mob.class, new NamespacedKey(SavingPrivateRahya.PLUGIN, key));

    }
    public GoalKey<Mob> getKey()
    {
        return key;
    }
}
