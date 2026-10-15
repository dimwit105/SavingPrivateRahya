package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.WaveStatus;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.util.Vector;

import javax.swing.text.html.Option;
import java.util.EnumSet;
import java.util.Optional;

public class RetreatWaveGoal extends AbstractWaveGoal
{
    Location currentDestination;
    Pathfinder.PathResult currentPath;
    public RetreatWaveGoal(AssaultManager manager, Mob mob) {
        super(manager, mob, "assault_retreat");
    }
    @Override
    public boolean shouldActivate()
    {
        return manager.getStatus() == WaveStatus.RETREAT;
    }
    @Override
    public void start()
    {
        super.start();
        updateFleePath();
    }

    @Override
    public void stop() {
        super.stop();
        assaultMob.getPathfinder().stopPathfinding();
    }

    @Override
    public void tick() {
        super.tick();
        assaultMob.setTarget(null);
        if(!assaultMob.getPathfinder().hasPath())
            updateFleePath();
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE);
    }

    private void updateFleePath()
    {
        currentDestination = fleePathDestination();
        currentPath = getFleePath(currentDestination);
        if(currentPath != null)
            assaultMob.getPathfinder().moveTo(currentPath);
    }

    private Pathfinder.PathResult getFleePath(Location destination)
    {
        if(destination == null)
            return null;

        World w = destination.getWorld();
        for(int y = -5; y < 5; y++)
        {
            Block block = w.getBlockAt(destination.getBlockX(), destination.getBlockY() + y, destination.getBlockZ());
            if(block.getType().isCollidable() && block.getRelative(BlockFace.UP, 2).getType().isAir())
            {
                 return assaultMob.getPathfinder().findPath(destination.clone().add(0, y, 0));
            }
        }
        return null;
    }

    private Location fleePathDestination()
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
            return null;

        Vector directionFromThem = current.toVector().subtract(nearestPlayer.getLocation().toVector()).normalize();
        directionFromThem = GameMath.restrictNormalizedVectorToPitchRange(directionFromThem, 30);
        Location thirtyTwoBlocksAway = current.clone().add(directionFromThem.multiply(32));
        return thirtyTwoBlocksAway;
    }

}
