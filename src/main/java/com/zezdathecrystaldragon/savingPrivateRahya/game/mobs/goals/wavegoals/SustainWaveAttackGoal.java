package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.WaveStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.EnumSet;

public class SustainWaveAttackGoal extends AbstractWaveGoal
{

    public SustainWaveAttackGoal(AssaultManager manager, Mob mob) {
        super(manager, mob, "sustain_attack");
    }

    @Override
    public boolean shouldActivate() {
        return manager.getStatus() == WaveStatus.SUSTAIN;
    }

    @Override
    public void tick()
    {
        super.tick();
        if(!(assaultMob.getTarget() instanceof Player))
        {
            var currentLoc = assaultMob.getLocation();
            var targetList = manager.getValidTargets();
            Player currentTarget = targetList.getFirst();
            for(Player player : targetList)
            {
                if(player.getLocation().distanceSquared(currentLoc) < currentTarget.getLocation().distanceSquared(currentLoc))
                {
                    currentTarget = player;
                }
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(assaultMob, currentTarget, EntityTargetEvent.TargetReason.CUSTOM);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled())
                assaultMob.setTarget(currentTarget);

        }
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }
}
