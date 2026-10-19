package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.AssaultManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.RallyLocation;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.WaveStatus;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.logging.Level;

public class BuildWaveRallyGoal extends AbstractWaveGoal
{
    private final Consumer<Location> rallyUpdater = this::onRallyLocationChanged;
    private boolean hasBeenAttackedByPlayer = false;
    private int nearnessCheck = 0;

    public BuildWaveRallyGoal(AssaultManager manager, Mob mob)
    {
        super(manager, mob, "assault_rally");
    }

    @Override
    public boolean shouldActivate()
    {
        return manager.getStatus() == WaveStatus.BUILD && manager.game.getVip().flatMap(Participant::getPlayer).isPresent();
    }

    @Override
    public boolean shouldStayActive()
    {
        var lastDamageEvent = assaultMob.getLastDamageCause();
        if (hasBeenAttackedByPlayer)
            return false;

        if(lastDamageEvent != null && lastDamageEvent.getDamageSource().getCausingEntity() instanceof Player)
        {
            hasBeenAttackedByPlayer = true;
            return false;
        }
        return super.shouldStayActive();
    }

    @Override
    public void start()
    {
        super.start();
        updatePathing(RallyLocation.induceRandomVariation(manager.rallyLocation.getCurrentRallyLocation()));
    }

    @Override
    public void stop()
    {
        super.stop();
        assaultMob.setTarget(null);
        manager.rallyLocation.silenceRallyLocationChanges(rallyUpdater);
    }

    @Override
    public void tick()
    {
        super.tick();
        Pathfinder pathfinder = assaultMob.getPathfinder();
        if (pathfinder.getCurrentPath() == null)
            updatePathing(RallyLocation.induceRandomVariation(manager.rallyLocation.getCurrentRallyLocation()));
        nearnessCheck++;
        if(nearnessCheck > 50)
        {
            nearnessCheck = 0;
            if(!assaultMob.getLocation().getNearbyPlayers(8).isEmpty())
            {
                hasBeenAttackedByPlayer = true;
            }
        }
    }

    private void updatePathing(Location newLoc)
    {
        Pathfinder pathfinder = assaultMob.getPathfinder();
        pathfinder.stopPathfinding();
        boolean success = pathfinder.moveTo(RallyLocation.induceRandomVariation(newLoc));
        //SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO,  "Rally pathing result: " + String.valueOf(success));
        //if(success)
            //assaultMob.addPotionEffect(PotionEffectType.GLOWING.createEffect(300, 0));
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.TARGET);
    }

    private void onRallyLocationChanged(Location newLoc)
    {
        updatePathing(newLoc);
    }
}
