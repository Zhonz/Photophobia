package com.zzandbrokensnake.photophobia.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.effects.ConcealmentEffect;

public class StatusEffectRegistry {
    public static final StatusEffect CONCEALMENT_EFFECT = new ConcealmentEffect();

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(PhotophobiaMod.MOD_ID, "concealment"),
                CONCEALMENT_EFFECT);
        PhotophobiaMod.LOGGER.info("Status effects registered");
    }
}
