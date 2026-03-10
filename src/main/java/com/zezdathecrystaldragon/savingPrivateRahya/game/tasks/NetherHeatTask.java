package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Level;

public class NetherHeatTask extends CancellableRunnable
{
    Game game;
    private int heat = 0;
    private int coolingTimer = 0;
    public final int heatEffectsStarting;
    private final int coolingThreshold;
    private ArrayList<PiglinSiegeTask> siegers = new ArrayList<PiglinSiegeTask>();

    public NetherHeatTask(Game game)
    {
        this.game = game;
        this.coolingThreshold = (int) Math.ceil(60 / GameMath.getHarmonicNumber(game.getParticipants().size()));
        heatEffectsStarting = 64 + game.getParticipants().size()*64;
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(this, 0, 20);
    }

    @Override
    public void run()
    {
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Nether heat is %d of %d", heat, heatEffectsStarting));
        if(coolingTimer > coolingThreshold)
        {
            if(heat < heatEffectsStarting)
                heat = 0;
            else
                heat--;
        }
        else
        {
            coolingTimer++;
        }
    }
    public int getHeatValue()
    {
        return heat;
    }
    public void incrementHeat()
    {
        cleanSiegerList();
        heat++;
        coolingTimer = 0;
        int excess = heat - heatEffectsStarting;
        if(heat > heatEffectsStarting && siegers.size() < game.getParticipants().size() && SavingPrivateRahya.RAND.nextInt(heatEffectsStarting*2) < excess)
        {
            PiglinSiegeTask sieger = spawnSieger();
            if(sieger != null)
                siegers.add(sieger);
        }
    }
    private PiglinSiegeTask spawnSieger()
    {
        ArrayList<Player> potentialTargets = new ArrayList<>();
        for(Participant part : game.getParticipants().values())
        {
            if(part.getPlayer() == null || part.getPlayer().getWorld().getEnvironment() != World.Environment.NETHER || part.isEliminated())
                continue;
            potentialTargets.add(part.getPlayer());
        }
        if(potentialTargets.isEmpty())
            return null;

        Player target = potentialTargets.get(SavingPrivateRahya.RAND.nextInt(potentialTargets.size()));
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
                spawnLocation = new Location(target.getWorld(), attempt.getBlockX(), y, attempt.getBlockZ());
                break;
            }
        }
        if(spawnLocation == null)
            return null;
        Piglin pb = target.getWorld().spawn(spawnLocation, Piglin.class);
        heat -= heatEffectsStarting/2;
        Bukkit.broadcast(Component.text("You hear the distant sound of blocks breaking"));
        return new PiglinSiegeTask(pb, target);
    }
    private void cleanSiegerList()
    {
        siegers.removeIf(CancellableRunnable::isCancelled);
    }
}
