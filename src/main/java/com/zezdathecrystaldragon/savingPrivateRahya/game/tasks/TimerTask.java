package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class TimerTask extends CancellableRunnable
{
    Game game;
    int secondsRemaining;
    int secondsMaximum;
    int maximumOvertime;
    public static final NamespacedKey TIMER_BAR = new NamespacedKey(SavingPrivateRahya.PLUGIN, "timer_bar");
    final Component name = Component.text("Nether Stability");
    BossBar visualTimer;
    private boolean revamped = false;
    private boolean timeDamaged = false;

    public TimerTask(Game game, int seconds)
    {
        this.game = game;
        this.secondsRemaining = seconds;
        this.secondsMaximum = seconds;
        this.maximumOvertime = (int) Math.floor(seconds * -0.5D);
        visualTimer = BossBar.bossBar(name, 1.0F, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_6);
        sendBossBars();
    }

    @Override
    public void cancel()
    {
        super.cancel();
        SavingPrivateRahya.PLUGIN.getServer().hideBossBar(visualTimer);
    }

    @Override
    public void run()
    {
        secondsRemaining--;
        if(timeDamaged) {
            visualTimer.color(BossBar.Color.RED);
            timeDamaged = false;
        }
        else
            visualTimer.color(revamped ? BossBar.Color.YELLOW : BossBar.Color.WHITE);

        if(secondsRemaining >= 0)
            visualTimer.progress((float) secondsRemaining /(float) secondsMaximum);
        else
            revampTimer();
        if(secondsRemaining <= maximumOvertime)
        {
            game.endGame(GameEndReason.TIMER_EXHAUSTED);
        }
    }

    private void revampTimer()
    {
        visualTimer.progress((float) Math.abs(secondsRemaining) / (Math.abs(maximumOvertime)));
        if(!revamped)
        {
            visualTimer.name(Component.text("Impending doom approaches"));
            visualTimer.color(BossBar.Color.YELLOW);
            game.nether.getWorldBorder().changeSize(game.extractionZoneTotal, maximumOvertime * -20L);
            revamped = true;
        }
    }

    public void start()
    {
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(this, 0, 20);
    }
    public int getSecondsRemaining() {return secondsRemaining;}

    public void damageTime(int amountToDamage)
    {
        secondsRemaining -= amountToDamage;
        timeDamaged = true;
    }
    public double getTimerPercentage()
    {
        return (double) secondsRemaining / secondsMaximum;
    }

    private void sendBossBars()
    {
        SavingPrivateRahya.PLUGIN.getServer().showBossBar(visualTimer);
    }
    public void onPlayerConnect(Player player)
    {
        player.showBossBar(visualTimer);
    }
}
