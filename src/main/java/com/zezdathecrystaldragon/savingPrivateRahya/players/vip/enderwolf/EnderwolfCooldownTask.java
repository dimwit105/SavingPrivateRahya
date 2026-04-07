package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf;

import com.zezdathecrystaldragon.savingPrivateRahya.players.util.VIPTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;

public class EnderwolfCooldownTask extends VIPTask
{
    Enderwolf enderwolf;
    protected EnderwolfCooldownTask(VeryImportantParticipant participant, Enderwolf enderwolf)
    {
        super(participant);
        this.enderwolf = enderwolf;
    }

    @Override
    public void run()
    {
        enderwolf.tickAbilities();
    }

    @Override
    public EnderwolfCooldownTask copy() {
        return new EnderwolfCooldownTask((VeryImportantParticipant) participant, enderwolf);
    }
}
