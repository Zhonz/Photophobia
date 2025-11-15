package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import me.shedaniel.autoconfig.AutoConfig;

public class ConfigRegistry {

    public static void register() {
        AutoConfig.register(PhotophobiaConfig.class, me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new);
        PhotophobiaMod.LOGGER.info("Config registry initialized");
    }
}
