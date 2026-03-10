package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.Location;
import org.bukkit.World;

public class GameMath
{
    public static int stochasticRounding(double numberToRound)
    {
        int result = (int) numberToRound;
        double chance = numberToRound - result;

        if (SavingPrivateRahya.RAND.nextDouble() < chance) {
            result++;
        }
        return result;
    }
    public static double getHarmonicNumber(int n)
    {
        if (n <= 0) return 0;
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToDouble(i -> 1.0 / i)
                .sum();
    }
    public static Location netherify(World nether, Location loc)
    {
        int netherX = loc.getBlockX() >> 3;
        int netherZ = loc.getBlockZ() >> 3;
        return new Location(nether, netherX, Math.clamp(loc.getBlockY(), 12, 112), netherZ);
    }
}
