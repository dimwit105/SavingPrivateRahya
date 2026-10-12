package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import org.bukkit.entity.Mob;

import java.util.EnumSet;

public class AssaultWaveRallyGoal extends AbstractWaveGoal
{

    public AssaultWaveRallyGoal(AssaultManager manager, Mob mob)
    {
        super(manager, mob);
    }

    @Override
    public boolean shouldActivate() {
        return false;
    }

    @Override
    public boolean shouldStayActive() {
        return super.shouldStayActive();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public GoalKey<Mob> getKey() {
        return null;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return null;
    }
}
