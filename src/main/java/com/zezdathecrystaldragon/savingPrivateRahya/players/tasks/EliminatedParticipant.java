package com.zezdathecrystaldragon.savingPrivateRahya.players.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;

public class EliminatedParticipant extends ParticipantTask
{
    int buffCooldown = 0;
    int debuffCooldown = 0;
    public EliminatedParticipant(Participant participant)
    {
        super(participant);
    }

    @Override
    public void run()
    {
        if(buffCooldown > 0)
            buffCooldown--;
        if(debuffCooldown > 0)
            debuffCooldown--;
    }

    public int getBuffCooldown() {
        return buffCooldown;
    }
    public int getDebuffCooldown() {
        return debuffCooldown;
    }
    public void setBuffCooldown()
    {
        buffCooldown = 60;
    }
    public void setDebuffCooldown()
    {
        debuffCooldown = 30;
    }
}
