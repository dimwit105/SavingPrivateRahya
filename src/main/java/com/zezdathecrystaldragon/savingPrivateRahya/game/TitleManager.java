package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;


import java.time.Duration;

public class TitleManager
{
    Title currentlyShownTitle = null;
    Game game;
    public TitleManager(Game game) {this.game = game;}

    public void sendTitleToOnlineOneSecond(Component what)
    {
        Title.Times times = Title.Times.times(Ticks.duration(0), Ticks.duration(20), Ticks.duration(0));
        Title title = Title.title(what, Component.empty(), times);
        for (Participant part : game.getParticipants().values())
        {
            if(part.getPlayer() == null)
                continue;
            showTitleToPlayer(part.getPlayer(), title);
        }
    }
    private void showTitleToPlayer(Player p, Title t)
    {
        if(currentlyShownTitle == null) {
            currentlyShownTitle = t;
            Title.Times times = t.times();
            Duration totalTitleLength = times.fadeIn().plus(times.stay().plus(times.fadeOut()));
            SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> currentlyShownTitle = null, (totalTitleLength.toMillis() / Ticks.SINGLE_TICK_DURATION_MS)-1);
        }

        if(currentlyShownTitle != t)
            return;
        p.showTitle(t);

    }
}
