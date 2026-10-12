package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.WaveStatus;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Optional;

public class RetreatWaveGoal extends AbstractWaveGoal
{
    public RetreatWaveGoal(AssaultManager manager, Mob mob) {
        super(manager, mob);
    }
    @Override
    public boolean shouldActivate() {
        return manager.getStatus() == WaveStatus.RETREAT;
    }
    @Override
    public void start()
    {
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

    private Optional<Location> fleePathDestination()
    {
        var current = assaultMob.getLocation();
        var playersNearby = current.getNearbyPlayers(32);
        Player nearestPlayer = null;
        for(Player p : playersNearby)
        {
            if(nearestPlayer == null) {
                nearestPlayer = p;
                continue;
            }
            if (current.distanceSquared(p.getLocation()) < current.distanceSquared(nearestPlayer.getLocation()))
            {
                nearestPlayer = p;
            }
        }
        if(nearestPlayer == null)
            return Optional.empty();

        Vector directionFromThem = current.toVector().subtract(nearestPlayer.getLocation().toVector()).normalize();
        Location thirtyTwoBlocksAway = current.clone().add(directionFromThem.multiply(32));
        return Optional.of(thirtyTwoBlocksAway);
    }

}
