package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault;

import java.util.Arrays;

public enum WaveStatus
{
    RETREAT(120),
    RECON (60),
    ANTICIPATION(120),
    BUILD(30),
    SUSTAIN(-1);

    private int duration;
    public final static int totalDuration = getTotalDuration();
    WaveStatus(int duration)
    {
        this.duration = duration;
    }
    public int getDuration()
    {
        return duration;
    }

    public static WaveStatus getStatusFromTime(int time)
    {
        WaveStatus[] statuses = WaveStatus.values();
        int index = 0;
        for (WaveStatus val : statuses)
        {
            time -= val.duration;
            if(time < 0)
                return statuses[index];
            index++;
        }

        return SUSTAIN;
    }
    public static int getTotalDuration()
    {
        int total = 0;
        for(WaveStatus val : WaveStatus.values())
        {
            if(val.duration > 0)
                total += val.duration;
        }
        return total;
    }
}
