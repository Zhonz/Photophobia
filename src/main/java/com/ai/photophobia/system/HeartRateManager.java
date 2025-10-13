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
     * 渲染心率HUD - 暂时注释掉，需要找到正确的渲染方法
     */
    /*
     * public static void renderHeartRateHUD() {
     * // 需要找到Minecraft 1.21.1中正确的渲染方法
     * // 暂时注释掉以避免编译错误
     * }
     */
}
