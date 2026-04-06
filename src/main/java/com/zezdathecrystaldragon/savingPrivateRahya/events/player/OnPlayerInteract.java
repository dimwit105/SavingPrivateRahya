package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;


public class OnPlayerInteract implements Listener
{
    ArrayList<PotionEffect> buffs = new ArrayList<>(List.of(
            new PotionEffect(PotionEffectType.REGENERATION, 55, 0, true,false),
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        Player p = event.getPlayer();
        Participant part = game.getParticipants().get(p.getUniqueId());

        if(event.getHand() != EquipmentSlot.HAND) return;
        if (part == null || !(event.getRightClicked() instanceof LivingEntity ent)) return;
        if (!part.isEliminated() || p.getGameMode() != GameMode.SPECTATOR) return;

        EliminatedParticipant ep = part.getEliminatedParticipant();
        boolean isBuff = ent instanceof Player;

        List<PotionEffect> pool = isBuff ? buffs : debuffs;
        int cooldown = isBuff ? ep.getBuffCooldown() : ep.getDebuffCooldown();
        String typeLabel = isBuff ? "Buff" : "Debuff";

        if (cooldown > 0) {
            p.sendMessage(Component.text(String.format("%s not quite ready yet, %d seconds until cooldown!", typeLabel, cooldown)));
            return;
        }

        PotionEffect effect = pool.get(SavingPrivateRahya.RAND.nextInt(pool.size()));
        ent.addPotionEffect(effect);

        if (isBuff) ep.setBuffCooldown(); else ep.setDebuffCooldown();
        if (isBuff) {Player buffed = (Player) ent; buffed.sendMessage(Component.text("You feel your lost friends helping you!")); }
        p.sendMessage(Component.text(String.format("Applied %s to %s!",
                effect.getType().getKey().getKey().toLowerCase().replace("_", " "),
                ent.getName())));
    }
}
