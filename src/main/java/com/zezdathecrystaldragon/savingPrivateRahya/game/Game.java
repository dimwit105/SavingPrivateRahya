package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.CountdownTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.TimerTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.VeryImportantParticipant;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game
{
    HashMap<UUID, Participant> participants = new HashMap<UUID, Participant>();
    VeryImportantParticipant vip;
    GameState currentState = GameState.LOBBY;
    TimerTask time = new TimerTask(this, 60*60);
    TitleManager titles = new TitleManager(this);
    public Game() {}

    public void setVeryImportantParticipant(Participant participant)
    {
        this.vip = participant.toVIP();
    }

    /**
     * Adds a player as a participant to the game, if they are not already a participant.
     * @param player the player to add as a participant
     * @return whether or not the player was actually added to the list.
     */
    public boolean addParticipant(Player player)
    {
        if(isPreGame() && !participants.containsKey(player.getUniqueId()))
        {
            participants.put(player.getUniqueId(), new Participant(player.getUniqueId()));
            return true;
        }
        return false;
    }
    public void startGame()
    {
        if(currentState != GameState.LOBBY)
            return;
        currentState = GameState.COUNTDOWN;
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(new CountdownTask(this, 20), 0, 20);
    }
    public void countDownFinished()
    {
        currentState = GameState.IN_PROGRESS;
        getTitles().sendTitleToOnlineOneSecond(Component.text("The game has begun!"));
    }

    public void endGame(GameEndReason reason)
    {
        if(currentState != GameState.IN_PROGRESS)
            return;

        if(reason == GameEndReason.VIP_DIED || reason == GameEndReason.TIMER_EXHAUSTED)
        {
            currentState = GameState.DEFEAT;
            for(Participant p : participants.values())
            {
                Player player = p.getPlayer();
                if(player == null || p.isEliminated())
                    continue;
                p.eliminate();
                long delay = SavingPrivateRahya.RAND.nextLong(100) + 50;
                SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> {
                    player.getWorld().createExplosion(player.getLocation(), 6f);
                    player.setHealth(0);
                }, delay);
            }
        }
        if(reason == GameEndReason.CANCELLED)
        {
            currentState = GameState.LOBBY;
            clearParticipants();
            World overworld = Bukkit.getWorlds().stream()
                    .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                    .findFirst()
                    .orElse(Bukkit.getWorlds().getFirst());

            for(Player p : Bukkit.getOnlinePlayers())
            {
                addParticipant(p);
                p.teleportAsync(overworld.getSpawnLocation());
            }
        }
        if(reason == GameEndReason.VICTORY)
        {
            currentState = GameState.VICTORY;
            clearParticipants();
        }
    }

    private void clearParticipants()
    {
        for(Participant p : participants.values())
        {
            p.cancelAllTasks();
        }
        participants.clear();
    }

    public void makeVIP(Player p)
    {
        if(currentState != GameState.LOBBY)
            return;

        Participant part = participants.get(p.getUniqueId());
        if(vip != null)
        {
            participants.put(vip.getID(), vip.unVIP());
        }
        vip = part.toVIP();
        participants.put(part.getID(), vip);
    }
    // ============================================== GETTERS/SETTERS PAST THIS POINT ===================================================================
    public boolean isPreGame()
    {
        return currentState == GameState.LOBBY || currentState == GameState.COUNTDOWN;
    }

    /**
     * Gets the VIP, can be null if the game is not started
     * @return the VIP
     */
    @Nullable
    public VeryImportantParticipant getVip()
    {
        return vip;
    }
    public final Map<UUID, Participant> getParticipants()
    {
        return Collections.unmodifiableMap(participants);
    }
    public TimerTask getTime() {return time;}
    public TitleManager getTitles() {return titles;}
}
