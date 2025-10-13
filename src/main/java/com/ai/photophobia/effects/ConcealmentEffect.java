package com.ai.photophobia.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * 藏匿效果 - 让玩家对怪物隐身
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class ConcealmentEffect extends StatusEffect {
    public ConcealmentEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x2D2D2D);
    }
}
