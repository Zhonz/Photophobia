package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Config(name = "photophobia")
public class PhotophobiaConfig implements ConfigData {

        @ConfigEntry.Category("general")
        @ConfigEntry.Gui.TransitiveObject
        public General general = new General();

        @ConfigEntry.Category("heart_rate")
        @ConfigEntry.Gui.TransitiveObject
        public HeartRate heartRate = new HeartRate();

        @ConfigEntry.Category("effects")
        @ConfigEntry.Gui.TransitiveObject
        public Effects effects = new Effects();

        public static class General {
                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.Gui.RequiresRestart
                public boolean enableMainStory = true;

                @ConfigEntry.Gui.Tooltip
                public boolean enableHeartRateSystem = true;

                @ConfigEntry.Gui.Tooltip
                public boolean enableMonsterAI = true;
        }

        public static class HeartRate {
                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 0, max = 300)
                public double heartRateMultiplier = 1.0;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
                public int baseHeartRate = 30;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
                public int maxHeartRate = 100;
        }

        public static class Effects {
                @ConfigEntry.Gui.Tooltip
                public boolean enableCriticalVisionEffect = true;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
                public int criticalVisionThreshold = 5;

                @ConfigEntry.Gui.Tooltip
                public boolean enableConcealmentEffect = true;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 30)
                public int concealmentDuration = 10;
        }

        public static PhotophobiaConfig getConfig() {
                return AutoConfig.getConfigHolder(PhotophobiaConfig.class).getConfig();
        }

        public static void register() {
                AutoConfig.register(PhotophobiaConfig.class, JanksonConfigSerializer::new);
                PhotophobiaMod.LOGGER.info("Config registry initialized");
        }
}
