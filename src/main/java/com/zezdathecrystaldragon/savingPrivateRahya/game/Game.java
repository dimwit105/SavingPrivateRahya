package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.CountdownTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.NetherHeatTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.TimerTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.WorldModifier;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Game
{
    HashMap<UUID, Participant> participants = new HashMap<UUID, Participant>();
    VeryImportantParticipant vip;
    GameState currentState = GameState.LOBBY;
    //TODO Make magic numbers configurable
    public final int vipDistance = 1500;
    public final int extractionZone = 64;
    public final int extractionZoneBuffer = 8;
    public final int extractionZoneTotal = extractionZone + extractionZoneBuffer;

    public final int baseTime = 45*60;

    private final TimerTask time;
    private CountdownTask countdownTask;
    private NetherHeatTask heat;
    public final TitleManager titles = new TitleManager(this);
    public WorldModifier wm;
    public final World overworld;
    public final World nether;
    public Game()
    {
        overworld = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElse(Bukkit.getWorlds().getFirst());
        nether = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NETHER)
                .findFirst()
                .orElse(Bukkit.getWorlds().getFirst());
        overworld.setSpawnLocation(0, 72, 0);
        for(Player p : Bukkit.getOnlinePlayers())
        {
            addParticipant(p);
            p.teleportAsync(overworld.getSpawnLocation());
        }
        nether.getWorldBorder().setCenter(0, 0);
        nether.getWorldBorder().changeSize(vipDistance*3.0F, 1);
        nether.setDifficulty(Difficulty.HARD);
        overworld.setDifficulty(Difficulty.HARD);
        wm = new WorldModifier(this);
        time = new TimerTask(this, baseTime);
        try{
            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("Health", Criteria.HEALTH, Component.text("health"), RenderType.HEARTS);
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        catch (IllegalArgumentException e)
        {
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "There's already a scoreboard objective named health, skipping");
        }

    }

    public Game newGame()
    {
        time.cancel();
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        clearParticipants();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.setGameMode(GameMode.SPECTATOR);
        }
        return new Game();
    }

    public void startGame(CommandSender starter)
    {
        if(currentState != GameState.LOBBY){
            starter.sendMessage("The game has already started! Not in lobby state!");
            return;}
        if(vip == null){
            starter.sendMessage("We need a VIP to start the game!");
            return;}
        if(!wm.isReady()){
            starter.sendMessage("We are still looking for a suitable spot to place the structures! Please try again later, or check the console for long-run logs.");
            return;
        }
        heat = new NetherHeatTask(this);

        currentState = GameState.COUNTDOWN;
        countdownTask = new CountdownTask(this, 3);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(countdownTask, 0, 20);
    }
    public void countDownFinished()
    {
        if(currentState != GameState.COUNTDOWN)
            return;

        currentState = GameState.IN_PROGRESS;
        getTitles().sendTitleToOnlineOneSecond(Component.text("The game has begun!"));
        for(Participant part : participants.values())
        {
            part.beginGame();
        }
        time.start();
    }

    public void endGame(GameEndReason reason)
    {
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, "Trying to end the game because " + reason.toString() + " in gamestate " + currentState.toString());
        if(currentState != GameState.IN_PROGRESS)
            return;

        if(reason == GameEndReason.VIP_DIED || reason == GameEndReason.TIMER_EXHAUSTED)
        {
            currentState = GameState.DEFEAT;
            titles.sendTitleToOnlineOneSecond(Component.text("The VIP has been slain!"));
            time.cancel();
            for(Participant p : participants.values())
            {
                Player player = p.getPlayer();
                if(player == null)
                    continue;

                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_7, 1, 1);

                if(p.isEliminated())
                    continue;
                p.eliminate();
                long delay = SavingPrivateRahya.RAND.nextLong(100) + 50;
                SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> {
                    if(player == null)
                        return;
                    player.setHealth(0.001953125D);
                    player.getWorld().createExplosion(player.getLocation(), 9f);
                }, delay);
            }
        }
        if(reason == GameEndReason.CANCELLED)
        {
            currentState = GameState.CANCELLED;
            clearParticipants();
        }
        if(reason == GameEndReason.VICTORY)
        {
            currentState = GameState.VICTORY;
            clearParticipants();
        }
        time.cancel();
        nether.getWorldBorder().changeSize(30_000_000, 0);
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
            participants.put(player.getUniqueId(), new Participant(player.getUniqueId(), this));
            return true;
        }
        return false;
    }

    public Participant removeParticipant(UUID player)
    {
        return participants.remove(player);
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
    public NetherHeatTask getHeat() {return heat;}
}
