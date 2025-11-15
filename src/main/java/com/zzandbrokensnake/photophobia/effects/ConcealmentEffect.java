package com.zzandbrokensnake.photophobia.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ConcealmentEffect extends StatusEffect {
    public ConcealmentEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x2D2D2D);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Implementation will be added later
    }
}
