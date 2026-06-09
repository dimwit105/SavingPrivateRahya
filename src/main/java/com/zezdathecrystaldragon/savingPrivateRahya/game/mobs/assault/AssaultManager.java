package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault;

import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AssaultManager
{
    private WaveStatus status = WaveStatus.RECON;
    private Game game;

    private boolean started = false;
    private int cycleTimer = WaveStatus.RETREAT.getDuration();

    private int maxSpawnsPerTick;
    private int mobsSpawnedThisTick = 0;

    private int maxKillsPerSustain;

    public AssaultManager(Game game)
    {
        this.game = game;
        int participants = game.getParticipants().size();
        maxSpawnsPerTick = participants * 3;
        maxKillsPerSustain = (int) GameMath.getHarmonicNumber(participants*2);
    }

    public void tick()
    {
        if(!started)
            return;

        cycleTimer++;
        status = WaveStatus.getStatusFromTime(cycleTimer);
    }
    public void assaultMobKilled()
    {
        maxKillsPerSustain--;
        if(maxKillsPerSustain <= 0)
            endSustainWave();
    }


    private void endSustainWave()
    {
        cycleTimer = 0;
        status = WaveStatus.RETREAT;
    }
    private ArrayList<Player> getValidTargets()
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
}
