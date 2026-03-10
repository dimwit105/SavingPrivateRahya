package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.aura;

import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class VIPEffectAura
{
    public record AuraEffects(int minLevel, PotionEffectType type, int amp) {
        public PotionEffect toEffect() {return new PotionEffect(type, 40, amp, true, false);}
    }
    public static final List<AuraEffects> LEVELS = List.of(
            new AuraEffects(1, PotionEffectType.HASTE, 0),
            new AuraEffects(2, PotionEffectType.JUMP_BOOST, 0),
            new AuraEffects(3, PotionEffectType.SLOW_FALLING, 0),
            new AuraEffects(4, PotionEffectType.HASTE, 1),
            new AuraEffects(5, PotionEffectType.JUMP_BOOST, 1),
            new AuraEffects(6, PotionEffectType.RESISTANCE, 0)
    );
    private final VeryImportantParticipant vip;
    private int level = 0;
    public VIPEffectAura(VeryImportantParticipant veryImportantParticipant)
    {
        this.vip = veryImportantParticipant;
        vip.addTask(new ApplyAuraTask(vip));
    }
    public int getLevel() {return level;}
    public void levelUp() { level++; if(level > 6 ) level = 6;}
}
