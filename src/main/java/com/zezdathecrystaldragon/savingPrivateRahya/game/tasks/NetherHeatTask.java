package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.MobTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.PiglinSiegeTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Piglin;
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
    boolean canQuickCool = true;

    public NetherHeatTask(Game game)
    {
        this.game = game;
        this.coolingThreshold = (int) Math.ceil(120 / (1 + GameMath.getHarmonicNumber(game.getParticipants().size())));
        heatEffectsStarting = 64 + game.getParticipants().size()*64;
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(this, 0, 20);
    }

    @Override
    public void run()
    {
        if(coolingTimer > coolingThreshold)
        {
            if(canQuickCool)
                heat = 0;
            else if(heat > 0)
                heat-= Math.max(GameMath.stochasticRounding((double) heat / heatEffectsStarting), 1);
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
    public void incrementHeat() {

        heat++;
        coolingTimer = 0;
        int excess = heat - heatEffectsStarting;
        if (excess > 0) {
            canQuickCool = false;
        }
        game.getMobs().rollSpawnSieger();
    }

}
