package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

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

        int ringLayer = (int) Math.ceil((Math.sqrt(gameIndex + 1) - 1) / 2);
        int ringSideLength = 2 * ringLayer;
        int maxIndexInRing = (int) Math.pow(ringSideLength + 1, 2);
        int gridX, gridZ;

        if (gameIndex >= maxIndexInRing - ringSideLength) {
            gridX = ringLayer - (maxIndexInRing - gameIndex); gridZ = -ringLayer;
        } else if (gameIndex >= maxIndexInRing - 2 * ringSideLength) {
            gridX = -ringLayer; gridZ = -ringLayer + (maxIndexInRing - ringSideLength - gameIndex);
        } else if (gameIndex >= maxIndexInRing - 3 * ringSideLength) {
            gridX = -ringLayer + (maxIndexInRing - 2 * ringSideLength - gameIndex); gridZ = ringLayer;
        } else {
            gridX = ringLayer; gridZ = ringLayer - (maxIndexInRing - 3 * ringSideLength - gameIndex);
        }

        return new Location(overworld, gridX * cellSize, 64, gridZ * cellSize);
    }

    public static Vector restrictNormalizedVectorToPitchRange(Vector normalizedVector, double maxAngleDegrees)
    {
        double x = normalizedVector.getX();
        double y = normalizedVector.getY();
        double z = normalizedVector.getZ();

        double maxSin = Math.sin(Math.toRadians(maxAngleDegrees));

        if (Math.abs(y) > maxSin)
        {
            double newY = Math.signum(y) * maxSin;

            double oldHorizontalLength = Math.sqrt(x * x + z * z);

            if (oldHorizontalLength > 0.00390625F) {
                double newHorizontalLength = Math.cos(Math.toRadians(maxAngleDegrees));
                double scale = newHorizontalLength / oldHorizontalLength;
                x *= scale;
                z *= scale;
            }
            y = newY;
        }
        return new Vector(x,y,z);
    }


}
