package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.enchantments.NightWatcherEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EnchantmentRegistry {

    public static final Enchantment NIGHT_WATCHER = new NightWatcherEnchantment(
            Enchantment.Rarity.RARE,
            EnchantmentTarget.BREAKABLE,
            new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });

    public static void register() {
        Registry.register(Registries.ENCHANTMENT,
                new Identifier(PhotophobiaMod.MOD_ID, "night_watcher"),
                NIGHT_WATCHER);
        PhotophobiaMod.LOGGER.info("Enchantment registry initialized");
    }
}
