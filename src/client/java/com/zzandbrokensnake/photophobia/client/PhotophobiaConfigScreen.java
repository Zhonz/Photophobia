package com.zzandbrokensnake.photophobia.client;

import com.zzandbrokensnake.photophobia.registry.PhotophobiaConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PhotophobiaConfigScreen {
    
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.autoconfig.photophobia.title"));
        
        // 设置保存回调
        builder.setSavingRunnable(() -> {
            // 配置会在用户点击保存时自动保存
        });
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
        // 创建通用设置分类
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.autoconfig.photophobia.category.general"));
        
        // 通用设置选项
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.general.enableMainStory"), 
                        PhotophobiaConfig.getConfig().general.enableMainStory)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.general.enableMainStory.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().general.enableMainStory = newValue)
                .build());
        
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.general.enableHeartRateSystem"), 
                        PhotophobiaConfig.getConfig().general.enableHeartRateSystem)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.general.enableHeartRateSystem.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().general.enableHeartRateSystem = newValue)
                .build());
        
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.general.enableMonsterAI"), 
                        PhotophobiaConfig.getConfig().general.enableMonsterAI)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.general.enableMonsterAI.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().general.enableMonsterAI = newValue)
                .build());
        
        // 创建心率系统分类
        ConfigCategory heartRate = builder.getOrCreateCategory(Text.translatable("text.autoconfig.photophobia.category.heart_rate"));
        
        // 心率系统选项
        heartRate.addEntry(entryBuilder.startDoubleField(Text.translatable("text.autoconfig.photophobia.option.heartRate.heartRateMultiplier"), 
                        PhotophobiaConfig.getConfig().heartRate.heartRateMultiplier)
                .setDefaultValue(1.0)
                .setMin(0.0)
                .setMax(3.0)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.heartRate.heartRateMultiplier.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().heartRate.heartRateMultiplier = newValue)
                .build());
        
        heartRate.addEntry(entryBuilder.startIntField(Text.translatable("text.autoconfig.photophobia.option.heartRate.baseHeartRate"), 
                        PhotophobiaConfig.getConfig().heartRate.baseHeartRate)
                .setDefaultValue(30)
                .setMin(1)
                .setMax(100)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.heartRate.baseHeartRate.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().heartRate.baseHeartRate = newValue)
                .build());
        
        heartRate.addEntry(entryBuilder.startIntField(Text.translatable("text.autoconfig.photophobia.option.heartRate.maxHeartRate"), 
                        PhotophobiaConfig.getConfig().heartRate.maxHeartRate)
                .setDefaultValue(100)
                .setMin(1)
                .setMax(100)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.heartRate.maxHeartRate.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().heartRate.maxHeartRate = newValue)
                .build());
        
        // 创建视觉效果分类
        ConfigCategory effects = builder.getOrCreateCategory(Text.translatable("text.autoconfig.photophobia.category.effects"));
        
        // 视觉效果选项
        effects.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.effects.enableCriticalVisionEffect"), 
                        PhotophobiaConfig.getConfig().effects.enableCriticalVisionEffect)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.effects.enableCriticalVisionEffect.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().effects.enableCriticalVisionEffect = newValue)
                .build());
        
        effects.addEntry(entryBuilder.startIntField(Text.translatable("text.autoconfig.photophobia.option.effects.criticalVisionThreshold"), 
                        PhotophobiaConfig.getConfig().effects.criticalVisionThreshold)
                .setDefaultValue(5)
                .setMin(1)
                .setMax(10)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.effects.criticalVisionThreshold.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().effects.criticalVisionThreshold = newValue)
                .build());
        
        effects.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.effects.enableConcealmentEffect"), 
                        PhotophobiaConfig.getConfig().effects.enableConcealmentEffect)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.effects.enableConcealmentEffect.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().effects.enableConcealmentEffect = newValue)
                .build());
        
        effects.addEntry(entryBuilder.startIntField(Text.translatable("text.autoconfig.photophobia.option.effects.concealmentDuration"), 
                        PhotophobiaConfig.getConfig().effects.concealmentDuration)
                .setDefaultValue(10)
                .setMin(1)
                .setMax(30)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.effects.concealmentDuration.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().effects.concealmentDuration = newValue)
                .build());
        
        // 创建指令设置分类
        ConfigCategory commands = builder.getOrCreateCategory(Text.translatable("text.autoconfig.photophobia.category.commands"));
        
        // 指令设置选项
        commands.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.autoconfig.photophobia.option.commands.disableTimeCommand"), 
                        PhotophobiaConfig.getConfig().commands.disableTimeCommand)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.commands.disableTimeCommand.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().commands.disableTimeCommand = newValue)
                .build());
        
        commands.addEntry(entryBuilder.startStrField(Text.translatable("text.autoconfig.photophobia.option.commands.timeCommandMessage"), 
                        PhotophobiaConfig.getConfig().commands.timeCommandMessage)
                .setDefaultValue("时间指令已被禁用，永恒黑夜模式已激活")
                .setTooltip(Text.translatable("text.autoconfig.photophobia.option.commands.timeCommandMessage.@Tooltip"))
                .setSaveConsumer(newValue -> PhotophobiaConfig.getConfig().commands.timeCommandMessage = newValue)
                .build());
        
        return builder.build();
    }
}
