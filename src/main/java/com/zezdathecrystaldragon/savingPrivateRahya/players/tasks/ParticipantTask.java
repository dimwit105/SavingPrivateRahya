package com.zezdathecrystaldragon.savingPrivateRahya.players.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.tasks.CancellableRunnable;

public abstract class ParticipantTask extends CancellableRunnable
{
    Participant participant;
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
