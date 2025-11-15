package com.zzandbrokensnake.photophobia.system;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LightHeartRateSystem {

    private static final int LIGHT_THRESHOLD = 7; // 亮度阈值
    private static final int HEART_RATE_DECREASE_PER_SECOND = 3; // 每秒减少3心率

    /**
     * 处理玩家在亮处的心率减少
     */
    public static void processLightHeartRateDecrease(PlayerEntity player) {
        if (player.getWorld().isClient())
            return;

        // 每秒检查一次
        if (player.getWorld().getTime() % 20 != 0)
            return;

        BlockPos playerPos = player.getBlockPos();
        int lightLevel = player.getWorld().getLightLevel(playerPos);

        // 如果亮度大于阈值，减少心率
        if (lightLevel > LIGHT_THRESHOLD) {
            decreaseHeartRateInLight(player);
        }
    }

    /**
     * 在亮处减少心率
     */
    private static void decreaseHeartRateInLight(PlayerEntity player) {
        int currentHeartRate = HeartRateManager.getHeartRate(player.getUuid());

        // 如果心率高于正常水平，每秒减少3
        if (currentHeartRate > 60) {
            int newHeartRate = Math.max(60, currentHeartRate - HEART_RATE_DECREASE_PER_SECOND);
            HeartRateManager.forceHeartRate(player.getUuid(), newHeartRate);

            // 记录调试信息
            if (player.getWorld() instanceof ServerWorld) {
                PhotophobiaMod.LOGGER.debug("玩家 {} 在亮处，心率从 {} 减少到 {}",
                        player.getName().getString(), currentHeartRate, newHeartRate);
            }
        }
    }

    /**
     * 检查玩家是否在亮处
     */
    public static boolean isPlayerInBrightArea(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        int lightLevel = player.getWorld().getLightLevel(playerPos);
        return lightLevel > LIGHT_THRESHOLD;
    }

    /**
     * 获取当前亮度级别
     */
    public static int getCurrentLightLevel(PlayerEntity player) {
        return player.getWorld().getLightLevel(player.getBlockPos());
    }
}
