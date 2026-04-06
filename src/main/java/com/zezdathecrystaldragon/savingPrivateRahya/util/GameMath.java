package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

    public static Location getNewGameAnchor(World overworld, int gameIndex) {
        int cellSize = 32000;

        int k = (int) Math.ceil((Math.sqrt(gameIndex + 1) - 1) / 2);
        int t = 2 * k;
        int m = (int) Math.pow(t + 1, 2);
        int x, z;

        if (gameIndex >= m - t) {
            x = k - (m - gameIndex); z = -k;
        } else if (gameIndex >= m - 2 * t) {
            x = -k; z = -k + (m - t - gameIndex);
        } else if (gameIndex >= m - 3 * t) {
            x = -k + (m - 2 * t - gameIndex); z = k;
        } else {
            x = k; z = k - (m - 3 * t - gameIndex);
        }

        return new Location(overworld, x * cellSize, 64, z * cellSize);
    }


}
