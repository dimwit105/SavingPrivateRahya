package com.zezdathecrystaldragon.savingPrivateRahya.players.util;

import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;

public abstract class VIPTask extends ParticipantTask
{
    protected VIPTask(VeryImportantParticipant participant)
    {
        super(participant);
    }
    public void onDemote()
    {
        this.cancel();
    }
    public abstract VIPTask copy();
}
