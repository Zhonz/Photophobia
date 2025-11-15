package com.zzandbrokensnake.photophobia;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzandbrokensnake.photophobia.registry.StatusEffectRegistry;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;
import com.zzandbrokensnake.photophobia.registry.ItemGroupRegistry;
import com.zzandbrokensnake.photophobia.registry.EntityRegistry;
import com.zzandbrokensnake.photophobia.registry.NetworkRegistry;
import com.zzandbrokensnake.photophobia.registry.ConfigRegistry;
import com.zzandbrokensnake.photophobia.registry.EnchantmentRegistry;
import com.zzandbrokensnake.photophobia.system.WorldEventHandler;
import com.zzandbrokensnake.photophobia.system.DeathEventHandler;
import com.zzandbrokensnake.photophobia.system.EternalNightSystem;
import com.zzandbrokensnake.photophobia.world.gen.PhotophobiaWorldGen;

public class PhotophobiaMod implements ModInitializer {
    public static final String MOD_ID = "photophobia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Photophobia Mod");

        // Initialize all registries
        StatusEffectRegistry.register();
        ItemRegistry.register();
        ItemGroupRegistry.register();
        EntityRegistry.register();
        NetworkRegistry.register();
        ConfigRegistry.register();
        EnchantmentRegistry.register();

        // Initialize world generation
        PhotophobiaWorldGen.initialize();

        // Initialize world event handler
        WorldEventHandler.initialize();

        // Initialize death event handler
        DeathEventHandler.initialize();

        // Initialize eternal night system
        EternalNightSystem.initialize();

        LOGGER.info("Photophobia Mod initialized successfully");
    }
}
