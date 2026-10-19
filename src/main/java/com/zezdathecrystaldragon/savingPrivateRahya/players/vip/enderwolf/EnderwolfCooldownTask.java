package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.VIPTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;

public class EnderwolfCooldownTask extends VIPTask
{
    Enderwolf enderwolf;
    protected EnderwolfCooldownTask(VeryImportantParticipant participant, Enderwolf enderwolf)
    {
        super(participant);
        this.enderwolf = enderwolf;
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(participant.getPlayer().orElseThrow(), this, 0, 20);
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
