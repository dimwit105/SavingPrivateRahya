package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;


import java.time.Duration;

public class TitleManager {
    Title currentlyShownTitle = null;
    Game game;

    public TitleManager(Game game) {
        this.game = game;
    }

    public void sendTitleToOnlineOneSecond(Component what) {
        if (currentlyShownTitle == null) {
            Title.Times times = Title.Times.times(Ticks.duration(3), Ticks.duration(14), Ticks.duration(3));
            Title title = Title.title(what, Component.empty(), times);
            currentlyShownTitle = title;
            SavingPrivateRahya.PLUGIN.getServer().showTitle(title);
            Duration totalTitleLength = times.fadeIn().plus(times.stay().plus(times.fadeOut()));
            SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> currentlyShownTitle = null, (totalTitleLength.toMillis() / Ticks.SINGLE_TICK_DURATION_MS) - 1);
        }
    }
}
