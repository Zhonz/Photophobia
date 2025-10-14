package com.ai.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 心率管理器 - 处理玩家心率计算和效果
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class HeartRateManager {
    private static final Map<UUID, HeartRateData> playerHeartRates = new HashMap<>();

    public static class HeartRateData {
        private int currentRate = 30; // 20-40基础波动
        private long lastUpdateTick = 0;
        private boolean isOverloading = false;
        private long overloadStartTime = 0;

        public int getCurrentRate() {
            return currentRate;
        }

        public void setCurrentRate(int rate) {
            this.currentRate = rate;
        }

        public boolean isOverloading() {
            return isOverloading;
        }

        public void setOverloading(boolean overloading) {
            this.isOverloading = overloading;
        }
    }

    public static void initialize() {
        // 初始化代码
    }

    /**
     * 获取玩家的心率数据
     * 
     * @param playerUUID 玩家UUID
     * @return 心率数据，如果不存在则创建新的
     */
    public static HeartRateData getHeartRateData(UUID playerUUID) {
        return playerHeartRates.computeIfAbsent(playerUUID, k -> new HeartRateData());
    }

    /**
     * 强制设置玩家心率
     * 
     * @param player 目标玩家
     * @param rate   心率值
     */
    public static void forceHeartRate(PlayerEntity player, int rate) {
        HeartRateData data = getHeartRateData(player.getUuid());
        data.setCurrentRate(MathHelper.clamp(rate, 0, 100));
    }

    /**
     * 获取玩家心率
     * 
     * @param player 目标玩家
     * @return 心率值
     */
    public static int getHeartRate(PlayerEntity player) {
        HeartRateData data = getHeartRateData(player.getUuid());
        return data.getCurrentRate();
    }

    /**
     * 获取心脏动画帧
     * 
     * @param heartRate 当前心率
     * @param tickDelta 渲染间隔
     * @return 心脏动画帧索引
     */
    public static int getHeartAnimationFrame(int heartRate, float tickDelta) {
        // 根据心率计算动画速度
        float animationSpeed = heartRate / 60.0f; // 基础速度
        long time = System.currentTimeMillis();

        // 计算当前帧
        int frameCount = 8; // 假设有8帧动画
        int frame = (int) ((time / (1000 / animationSpeed)) % frameCount);

        return frame;
    }

    /**
     * 获取心脏颜色
     * 实现渐变变色效果：绿色→黄色→橙色→红色
     * 
     * @param heartRate    当前心率
     * @param isCalmEffect 是否处于宁神粉尘效果中
     * @return 心脏颜色 (0xAARRGGBB格式)
     */
    public static int getHeartColor(int heartRate, boolean isCalmEffect) {
        // 宁神粉尘效果：心脏图标变黄
        if (isCalmEffect) {
            return 0xFFFFFF55; // 黄色
        }

        // 根据心率范围计算渐变颜色
        if (heartRate <= HeartRateCalculator.MAX_SAFE_HEART_RATE) {
            // 60-160 BPM：绿色→黄色渐变
            float progress = (heartRate - HeartRateCalculator.BASE_RESTING_RATE_MIN) /
                    (float) (HeartRateCalculator.MAX_SAFE_HEART_RATE - HeartRateCalculator.BASE_RESTING_RATE_MIN);
            return interpolateColor(0xFF55FF55, 0xFFFFFF55, progress); // 绿色→黄色
        } else if (heartRate <= HeartRateCalculator.DANGER_HEART_RATE) {
            // 160-180 BPM：黄色→橙色渐变
            float progress = (heartRate - HeartRateCalculator.MAX_SAFE_HEART_RATE) /
                    (float) (HeartRateCalculator.DANGER_HEART_RATE - HeartRateCalculator.MAX_SAFE_HEART_RATE);
            return interpolateColor(0xFFFFFF55, 0xFFFFAA00, progress); // 黄色→橙色
        } else {
            // 180-200 BPM：橙色→红色渐变
            float progress = (heartRate - HeartRateCalculator.DANGER_HEART_RATE) /
                    (float) (HeartRateCalculator.FATAL_HEART_RATE - HeartRateCalculator.DANGER_HEART_RATE);
            return interpolateColor(0xFFFFAA00, 0xFFFF5555, progress); // 橙色→红色
        }
    }

    /**
     * 颜色插值计算
     * 
     * @param color1   起始颜色
     * @param color2   结束颜色
     * @param progress 进度 (0.0-1.0)
     * @return 插值后的颜色
     */
    private static int interpolateColor(int color1, int color2, float progress) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * 获取心脏大小缩放
     * 
     * @param heartRate 当前心率
     * @return 心脏大小缩放因子
     */
    public static float getHeartScale(int heartRate) {
        // 心率越高，心脏跳动幅度越大
        float baseScale = 1.0f;
        float pulseAmplitude = (heartRate / 100.0f) * 0.3f;

        // 模拟心跳脉冲
        long time = System.currentTimeMillis();
        float pulse = (float) Math.sin(time * 0.01f) * pulseAmplitude;

        return baseScale + pulse;
    }
}
