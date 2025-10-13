package com.ai.photophobia.registry;

import com.ai.photophobia.PhotophobiaMod;
import com.ai.photophobia.effects.ConcealmentEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 状态效果注册表
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class StatusEffectRegistry {
    public static final StatusEffect CONCEALMENT_EFFECT = new ConcealmentEffect();

    /**
     * 注册所有状态效果
     */
    public static void register() {
        Registry.register(Registries.STATUS_EFFECT,
                Identifier.of(PhotophobiaMod.MOD_ID, "concealment"),
                CONCEALMENT_EFFECT);

        PhotophobiaMod.LOGGER.info("状态效果注册完成");
    }
}
