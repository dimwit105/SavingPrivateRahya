package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.tasks.CancellableRunnable;
import net.kyori.adventure.text.Component;

public class CountdownTask extends CancellableRunnable
{
    Game game;
    int countDownTimer;
    public CountdownTask(Game game, int countDownFrom)
    {
        this.game = game;
        countDownTimer = countDownFrom;
    }
    @Override
    public void cancel()
    {
        game.countDownFinished();
        super.cancel();
    }
    @Override
    public void run()
    {
        if(countDownTimer <= 0) {
            cancel();
            return;
        }
        game.getTitles().sendTitleToOnlineOneSecond(Component.text("Game begins in %d seconds!".formatted(countDownTimer)));
        countDownTimer--;
    }
}
