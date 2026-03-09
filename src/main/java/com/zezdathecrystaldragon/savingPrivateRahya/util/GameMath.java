package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;

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
}
