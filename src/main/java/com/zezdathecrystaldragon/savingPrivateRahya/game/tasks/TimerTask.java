package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.tasks.CancellableRunnable;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class TimerTask extends CancellableRunnable
{
    Game game;
    int secondsRemaining;
    int secondsMaximum;
    public static final NamespacedKey TIMER_BAR = new NamespacedKey(SavingPrivateRahya.PLUGIN, "timer_bar");
    final Component name = Component.text("Nether Stability");
    BossBar visualTimer;
    private boolean revamped = false;

    public TimerTask(Game game, int seconds)
    {
        this.game = game;
        this.secondsRemaining = seconds;
        this.secondsMaximum = seconds;
        visualTimer = BossBar.bossBar(name, 1.0F, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_6);
        sendBossBars();
    }

    @Override
    public void run()
    {
        secondsRemaining--;
        if(secondsRemaining >= 0)
            visualTimer.progress((float) secondsRemaining /(float) secondsMaximum);
        else
            revampTimer();
    }

    private void revampTimer()
    {
        visualTimer.progress(Math.abs(secondsRemaining) / (secondsMaximum * 0.5F));
        if(!revamped)
        {
            visualTimer.name(Component.text("Impending doom approaches"));
            visualTimer.color(BossBar.Color.WHITE);
            revamped = true;
        }

    }

    public void start()
    {
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(this, 0, 20);
    }
    public int getSecondsRemaining() {return secondsRemaining;}

    private void sendBossBars()
    {
        for (Participant p : game.getParticipants().values())
        {
            Player player = p.getPlayer();
            if(player == null)
                continue;
            player.showBossBar(visualTimer);
        }
    }
    public void onPlayerConnect(Player player)
    {
        player.showBossBar(visualTimer);
    }
}
