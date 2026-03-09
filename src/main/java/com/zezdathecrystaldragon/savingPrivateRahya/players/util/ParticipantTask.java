package com.zezdathecrystaldragon.savingPrivateRahya.players.util;

import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;

public abstract class ParticipantTask extends CancellableRunnable
{
    protected Participant participant;
    protected ParticipantTask(Participant participant)
    {
        this.participant = participant;
    }
    @Override
    public void cancel()
    {
        participant.taskConcluded(this);
        super.cancel();
    }
}
