package com.ai.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家状态管理器 - 跟踪玩家的各种状态效果
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class PlayerStatusManager {
    private static final Map<UUID, PlayerStatusData> playerStatuses = new HashMap<>();

    public static class PlayerStatusData {
        private boolean isCalmEffectActive = false;
        private long calmEffectStartTime = 0;
        private long calmEffectDuration = 0;
        private boolean isConcealed = false;
        private boolean isBioluminescent = false;

        public boolean isCalmEffectActive() {
            return isCalmEffectActive;
        }

        public void setCalmEffectActive(boolean active, long duration) {
            this.isCalmEffectActive = active;
            this.calmEffectStartTime = System.currentTimeMillis();
            this.calmEffectDuration = duration;
        }

        public long getCalmEffectRemainingTime() {
            if (!isCalmEffectActive) {
                return 0;
            }
            long elapsed = System.currentTimeMillis() - calmEffectStartTime;
            return Math.max(0, calmEffectDuration - elapsed);
        }

        public boolean isConcealed() {
            return isConcealed;
        }

        public void setConcealed(boolean concealed) {
            this.isConcealed = concealed;
        }

        public boolean isBioluminescent() {
            return isBioluminescent;
        }

        public void setBioluminescent(boolean bioluminescent) {
            this.isBioluminescent = bioluminescent;
        }
    }

    /**
     * 获取玩家的状态数据
     */
    public static PlayerStatusData getPlayerStatusData(UUID playerUUID) {
        return playerStatuses.computeIfAbsent(playerUUID, k -> new PlayerStatusData());
    }

    /**
     * 应用宁神粉尘效果
     * 
     * @param player   目标玩家
     * @param duration 持续时间 (毫秒)
     */
    public static void applyCalmEffect(ServerPlayerEntity player, long duration) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        status.setCalmEffectActive(true, duration);

        // 立即降低心率
        int currentHeartRate = HeartRateManager.getHeartRate(player);
        int newHeartRate = Math.max(HeartRateCalculator.BASE_RESTING_RATE_MIN, currentHeartRate - 50);
        HeartRateManager.forceHeartRate(player, newHeartRate);

        player.sendMessage(net.minecraft.text.Text.literal("宁神粉尘生效，心率降低，心脏图标变黄"), true);
    }

    /**
     * 检查宁神粉尘效果是否激活
     */
    public static boolean isCalmEffectActive(PlayerEntity player) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        if (status.isCalmEffectActive()) {
            // 检查效果是否过期
            if (status.getCalmEffectRemainingTime() <= 0) {
                status.setCalmEffectActive(false, 0);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取宁神粉尘剩余时间
     */
    public static long getCalmEffectRemainingTime(PlayerEntity player) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        return status.getCalmEffectRemainingTime();
    }

    /**
     * 更新玩家状态
     */
    public static void updatePlayerStatuses() {
        // 定期检查状态效果是否过期
        for (PlayerStatusData status : playerStatuses.values()) {
            if (status.isCalmEffectActive() && status.getCalmEffectRemainingTime() <= 0) {
                status.setCalmEffectActive(false, 0);
            }
        }
    }

    /**
     * 设置藏匿状态
     */
    public static void setConcealed(PlayerEntity player, boolean concealed) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        status.setConcealed(concealed);
    }

    /**
     * 检查是否处于藏匿状态
     */
    public static boolean isConcealed(PlayerEntity player) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        return status.isConcealed();
    }

    /**
     * 设置生物荧光状态
     */
    public static void setBioluminescent(PlayerEntity player, boolean bioluminescent) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        status.setBioluminescent(bioluminescent);
    }

    /**
     * 检查是否处于生物荧光状态
     */
    public static boolean isBioluminescent(PlayerEntity player) {
        PlayerStatusData status = getPlayerStatusData(player.getUuid());
        return status.isBioluminescent();
    }

    /**
     * 清理玩家状态数据
     */
    public static void cleanupPlayerStatus(UUID playerUUID) {
        playerStatuses.remove(playerUUID);
    }
}
