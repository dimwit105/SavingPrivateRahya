package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.assault;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.parser.ParserImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RallyLocation
{
    private final Game game;
    private final AssaultManager am;
    private final Random random = SavingPrivateRahya.RAND;
    private Location currentLocation;
    //tiny little event bus so we can tell mobs the rally point has changed when it does
    private final List<Consumer<Location>> listeners = new ArrayList<>();
    public RallyLocation(Game game, AssaultManager am)
    {
        this.game = game;
        this.am = am;
    }

    public Location getCurrentRallyLocation()
    {
        if(currentLocation == null || currentLocation.getNearbyPlayers(32).isEmpty())
        {
            currentLocation = initRallyLocation();
            notifyListeners(currentLocation);
            return currentLocation;
        }
        return currentLocation;
    }
    public void listenToRallyLocationChanges(Consumer<Location> listener)
    {
        listeners.add(listener);
    }
    public void silenceRallyLocationChanges(Consumer<Location> listener)
    {
        listeners.remove(listener);
    }

    public static Location induceRandomVariation(Location pure)
    {
        int x = SavingPrivateRahya.RAND.nextInt(3) - SavingPrivateRahya.RAND.nextInt(2);
        int z = SavingPrivateRahya.RAND.nextInt(3) - SavingPrivateRahya.RAND.nextInt(2);

        return pure.clone().add(x, 0, z);
    }

    private void notifyListeners(Location newLoc)
    {
        for (Consumer<Location> listener : listeners)
        {
            listener.accept(newLoc);
        }
    }

    private Location initRallyLocation()
    {
        AtomicReference<Player> vipPlayer = new AtomicReference<>();
        game.getVip().flatMap(Participant::getPlayer).ifPresent(vipPlayer::set);

        if(vipPlayer.get() == null)
        {
            var participants = game.getParticipants();
            for(Participant part: participants.values())
            {
                part.getPlayer().ifPresent(player -> {
                    if (player.getWorld().getEnvironment() == World.Environment.NETHER)
                        vipPlayer.set(player);
                });
            }
        }
        if(vipPlayer.get() == null)
            throw new NullPointerException("Rally Location couldn't find an active nether player! This should never happen, as it's only called if there's an active game!");

        Location vipLoc = vipPlayer.get().getLocation();
        for(int attempt = 0; attempt < 20; attempt++)
        {
            Location candidate = getRandomPointInRange(vipLoc, 20,24);
            var playersNear = candidate.getNearbyPlayers(16);
            if(playersNear.isEmpty())
            {
                return refineRallyLocation(candidate);
            }
            for(Player p : playersNear)
            {
                Location subCandidate = getRandomPointInRange(p.getLocation(), 20, 24);
                if (subCandidate.getNearbyPlayers(16).isEmpty())
                    return refineRallyLocation(subCandidate);
            }
        }
        return getRandomPointInRange(vipLoc, 32, 40);
    }

    private Location refineRallyLocation(Location rallySpot)
    {
        World w = rallySpot.getWorld();
        for(int y = -5; y < 5; y ++)
        {
            boolean valid = true;
            Location checkingSpot = rallySpot.clone().add(0,y,0);
            if(!w.getBlockAt(checkingSpot).isSolid())
                continue;
            for(int chk = 1; chk < 2; chk++)
            {
                if(!w.getBlockAt(checkingSpot.clone().add(0,chk,0)).isEmpty()) {
                    valid = false;
                    break;
                }
            }
            if(valid)
                return checkingSpot;
        }
        return rallySpot;
    }
    private Location getRandomPointInRange(Location origin, int minRange, int maxRange)
    {
        double theta = random.nextDouble() * 2 * Math.PI;
        double r = minRange + (random.nextDouble() * (maxRange - minRange));

        int x = (int) ((r * Math.cos(theta)) + origin.getX());
        int z = (int) ((r * Math.sin(theta)) + origin.getZ());

        return new Location(origin.getWorld(), x, origin.getY(), z);
    }
}
