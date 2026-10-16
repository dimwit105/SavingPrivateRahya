package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault;

import com.destroystokyo.paper.entity.ai.VanillaGoal;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameState;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault.tasks.AssaultWaveTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals.BuildWaveRallyGoal;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals.RetreatWaveGoal;
import com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals.wavegoals.SustainWaveAttackGoal;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class AssaultManager
{
    public static final NamespacedKey ASSAULT_MOB = new NamespacedKey(SavingPrivateRahya.PLUGIN, "assault_mob");
    private WaveStatus status = WaveStatus.BUILD;
    public final Game game;
    private Random random = SavingPrivateRahya.RAND;
    public final RallyLocation rallyLocation;

    private boolean started = false;
    private int cycleTimer = WaveStatus.RETREAT.getDuration();

    private int maxSpawnsPerTick;

    private int maxKillsPerSustain = 100;
    private AssaultWaveTask tickTask;

    public AssaultManager(Game game)
    {
        this.game = game;
        rallyLocation = new RallyLocation(game, this);
        maxSpawnsPerTick = 10;
    }
    public void start()
    {
        started = true;
        maxKillsPerSustain = (int) (100);
        tickTask = new AssaultWaveTask(this);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(tickTask, 20, 20);
    }

    public void tick()
    {
        if(!started)
            return;

        if(game.getState() != GameState.IN_PROGRESS)
        {
            tickTask.cancel();
            return;
        }
        cycleTimer++;
        status = WaveStatus.getStatusFromTime(cycleTimer);

        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Assault wave status: " + status.toString());
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Rally Location: " + rallyLocation.getCurrentRallyLocation());

        if(status != WaveStatus.RETREAT)
        {
            var validTargets = getValidTargets();
            for(int spawns = 0; spawns < maxSpawnsPerTick;)
            {
                var randomTarget = validTargets.get(random.nextInt(validTargets.size()));
                SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Attempting spawns!");

                PigZombie mob = (PigZombie) spawnAroundTarget(randomTarget, EntityType.ZOMBIFIED_PIGLIN);
                if(mob != null)
                {
                    SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Mob spawned!: " + mob.getLocation());
                    spawns++;
                    mob.getPersistentDataContainer().set(ASSAULT_MOB, PersistentDataType.BOOLEAN, true);
                    var goals = Bukkit.getMobGoals();
                    goals.addGoal(mob, 0, new SustainWaveAttackGoal(this, mob));
                    goals.addGoal(mob, -1, new BuildWaveRallyGoal(this, mob));
                    goals.addGoal(mob, -2, new RetreatWaveGoal(this, mob));
                    goals.removeGoal(mob, VanillaGoal.AVOID_ENTITY);

                }
            }
        }
    }

    public WaveStatus getStatus()
    {
        return status;
    }
    public void assaultMobKilled()
    {
        maxKillsPerSustain--;
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Assault mob killed! " + maxKillsPerSustain + " kills remain!");
        if(maxKillsPerSustain <= 0)
            endSustainWave();
    }
    public ArrayList<Player> getValidTargets()
    {
        ArrayList<Player> validPlayers = new ArrayList<>();
        for(Participant part : game.getParticipants().values())
        {
            if(part.getPlayer().isEmpty())
                continue;
            Player player = part.getPlayer().get();
            if(player.getWorld().getEnvironment() == World.Environment.NETHER)
                validPlayers.add(player);
        }
        return validPlayers;
    }

    private void endSustainWave()
    {
        cycleTimer = 0;
        maxKillsPerSustain = 100;
        status = WaveStatus.RETREAT;
    }
    private Mob spawnAroundTarget(Player target, EntityType toSpawn)
    {
        Location loc = target.getLocation();
        Location spawnLocation = null;
        for(int attempts = 0; attempts < 66; attempts++)
        {
            Location attempt = loc.clone().add(-32 + SavingPrivateRahya.RAND.nextInt(64), 0, -32 + SavingPrivateRahya.RAND.nextInt(64));
            for(int y = Math.min(attempt.getBlockY()+32, 112); y > 32; y--)
            {
                Block block = target.getWorld().getBlockAt(attempt.getBlockX(), y, attempt.getBlockZ());
                if(!block.isSolid())
                    continue;
                if(block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).isSolid() || block.getWorld().getBlockAt(block.getLocation().add(0,2,0)).isSolid())
                    continue;
                if(!attempt.getNearbyPlayers(16).isEmpty())
                    continue;
                spawnLocation = new Location(target.getWorld(), attempt.getBlockX(), y+1, attempt.getBlockZ());
                break;
            }
        }
        if(spawnLocation == null)
            return null;
        return (Mob) target.getWorld().spawnEntity(spawnLocation, toSpawn);
    }

}
