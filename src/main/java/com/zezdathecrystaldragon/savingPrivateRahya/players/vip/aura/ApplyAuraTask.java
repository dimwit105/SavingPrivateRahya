package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.aura;

import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ApplyAuraTask extends ParticipantTask
{

    VeryImportantParticipant vip;
    public ApplyAuraTask(VeryImportantParticipant participant)
    {
        super(participant);
        vip = participant;
    }

    @Override
    public void run() {
        Player player = participant.getPlayer();
        int currentLevel = vip.getAura().getLevel();
        if (player == null || currentLevel <= 0) return;

        Map<PotionEffectType, PotionEffect> toApply = new HashMap<>();

        for (VIPEffectAura.AuraEffects stage : VIPEffectAura.LEVELS) {
            if (currentLevel >= stage.minLevel()) {
                PotionEffect existing = toApply.get(stage.type());
                // Only replace if this stage provides a higher amplifier
                if (existing == null || stage.amp() > existing.getAmplifier()) {
                    toApply.put(stage.type(), stage.toEffect());
                }
            }
        }

        Collection<PotionEffect> finalEffects = toApply.values();
        Collection<Player> nearby = player.getLocation().getNearbyPlayers(8.0);

        for (Player friend : nearby) {
            friend.addPotionEffects(finalEffects);
        }
    }
}
