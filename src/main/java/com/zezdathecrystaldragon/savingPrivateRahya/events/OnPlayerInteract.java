package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.EliminatedParticipant;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;


public class OnPlayerInteract implements Listener
{
    ArrayList<PotionEffect> buffs = new ArrayList<>(List.of(
            new PotionEffect(PotionEffectType.REGENERATION, 30, 0, true,false),
            new PotionEffect(PotionEffectType.ABSORPTION, 300, 0, true, false),
            new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0, true, false),
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, true,true)
    ));
    ArrayList<PotionEffect> debuffs = new ArrayList<>(List.of(
            new PotionEffect(PotionEffectType.WITHER, 200, 0, true,false),
            new PotionEffect(PotionEffectType.WEAKNESS, 300, 0, true, false),
            new PotionEffect(PotionEffectType.LEVITATION, 100, 0, true, false),
            new PotionEffect(PotionEffectType.SLOWNESS, 300, 0, true,true)
    ));
    Game game = SavingPrivateRahya.PLUGIN.getGame();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        Participant part = game.getParticipants().get(p.getUniqueId());

        if (part == null || !(event.getRightClicked() instanceof LivingEntity ent)) return;
        if (!part.isEliminated() || p.getGameMode() != GameMode.SPECTATOR) return;

        EliminatedParticipant ep = part.getEliminatedParticipant();
        boolean isBuff = ent instanceof Player;

        List<PotionEffect> pool = isBuff ? buffs : debuffs;
        int cooldown = isBuff ? ep.getBuffCooldown() : ep.getDebuffCooldown();
        String typeLabel = isBuff ? "Buff" : "Debuff";

        if (cooldown > 0) {
            p.sendActionBar(Component.text(String.format("%s not quite ready yet, %d seconds until cooldown!", typeLabel, cooldown)));
            return;
        }

        PotionEffect effect = pool.get(SavingPrivateRahya.RAND.nextInt(pool.size()));
        ent.addPotionEffect(effect);

        if (isBuff) ep.setBuffCooldown(); else ep.setDebuffCooldown();

        p.sendActionBar(Component.text(String.format("Applied %s to %s!",
                effect.getType().getKey().getKey().toLowerCase().replace("_", " "),
                ent.getName())));
    }
}
