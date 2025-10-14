package com.ai.photophobia.system;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * 心率计算器 - 根据各种因素计算玩家心率
 * 实现现实化心率机制
 * 
 * @author AI Developer
 * @version 2.0
 * @since 2024
 */
public class HeartRateCalculator {

    // 心率参数
    public static final int BASE_RESTING_RATE_MIN = 60;
    public static final int BASE_RESTING_RATE_MAX = 80;
    public static final int MAX_SAFE_HEART_RATE = 160;
    public static final int DANGER_HEART_RATE = 180;
    public static final int FATAL_HEART_RATE = 200;

    // 心率变化速度 (原设计的50%)
    public static final double HEART_RATE_RISE_SPEED = 0.5;
    public static final double HEART_RATE_RECOVERY_SPEED_SAFE = 8.0; // 安全区内
    public static final double HEART_RATE_RECOVERY_SPEED_UNSAFE = 3.0; // 安全区外

    /**
     * 计算玩家的当前心率
     * 
     * @param player 目标玩家
     * @return 计算后的心率值 (60-200)
     */
    public static int calculateHeartRate(PlayerEntity player) {
        // 基础静息心率 (60-80 BPM波动)
        int baseRate = BASE_RESTING_RATE_MIN
                + player.getRandom().nextInt(BASE_RESTING_RATE_MAX - BASE_RESTING_RATE_MIN + 1);

        // 威胁检测
        double threatBonus = calculateThreatBonus(player);

        // 受伤加成
        double injuryBonus = calculateInjuryBonus(player);

        // 环境因素
        double environmentBonus = calculateEnvironmentBonus(player);

        // 黑暗环境累积压力
        double darknessStress = calculateDarknessStress(player);

        int finalRate = (int) (baseRate + threatBonus + injuryBonus + environmentBonus + darknessStress);
        return MathHelper.clamp(finalRate, BASE_RESTING_RATE_MIN, FATAL_HEART_RATE);
    }

    /**
     * 计算威胁加成
     * 
     * @param player 目标玩家
     * @return 威胁加成值
     */
    private static double calculateThreatBonus(PlayerEntity player) {
        double threat = 0;
        List<LivingEntity> nearbyEntities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(player.getBlockPos()).expand(16),
                entity -> entity instanceof Monster);

        for (LivingEntity entity : nearbyEntities) {
            double distance = entity.squaredDistanceTo(player);
            Vec3d toPlayer = player.getPos().subtract(entity.getPos()).normalize();
            Vec3d lookVec = entity.getRotationVec(1.0f);

            double dotProduct = toPlayer.dotProduct(lookVec);
            boolean isBehind = dotProduct < -0.5; // 在背后

            // 首次看到怪物 +20 BPM，后续 +5 BPM
            double baseThreat = 5.0; // 后续威胁
            if (isFirstSighting(player, entity)) {
                baseThreat = 20.0; // 首次威胁
            }

            double distanceFactor = (16 - Math.sqrt(distance)) / 16.0;
            double threatFromEntity = distanceFactor * (isBehind ? 1.5 : 1.0) * baseThreat;

            threat += threatFromEntity;
        }

        return threat;
    }

    /**
     * 检查是否是首次看到怪物
     */
    private static boolean isFirstSighting(PlayerEntity player, LivingEntity entity) {
        // 这里可以添加更复杂的首次发现逻辑
        // 暂时简化实现
        return player.getRandom().nextFloat() < 0.3f; // 30%几率触发首次威胁
    }

    /**
     * 计算受伤加成
     * 
     * @param player 目标玩家
     * @return 受伤加成值
     */
    private static double calculateInjuryBonus(PlayerEntity player) {
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();

        // 受伤加成：+30 BPM
        if (player.hurtTime > 0) {
            return 30.0;
        }

        // 低血量加成 (<5心)：+40 BPM
        if (currentHealth <= 5.0f) {
            return 40.0;
        }

        // 血量百分比加成
        float healthPercentage = currentHealth / maxHealth;
        if (healthPercentage < 0.3f) {
            return 20.0; // 严重受伤
        } else if (healthPercentage < 0.6f) {
            return 10.0; // 中等受伤
        }

        return 0.0;
    }

    /**
     * 计算环境加成
     * 
     * @param player 目标玩家
     * @return 环境加成值
     */
    private static double calculateEnvironmentBonus(PlayerEntity player) {
        double environmentBonus = 0;

        // 高度加成
        if (player.getY() > 100) {
            environmentBonus += 5.0; // 高处增加心率
        }

        // 水下加成
        if (player.isSubmergedInWater()) {
            environmentBonus += 8.0; // 水下增加心率
        }

        return environmentBonus;
    }

    /**
     * 计算黑暗环境累积压力
     * 每分钟+5 BPM (累积压力)
     */
    private static double calculateDarknessStress(PlayerEntity player) {
        int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());
        if (lightLevel < 4) {
            // 黑暗环境每分钟+5 BPM
            long worldTime = player.getWorld().getTime();
            long minutesInDarkness = worldTime / 1200; // 每tick计算
            return Math.min(minutesInDarkness * 5.0, 30.0); // 最大累积30 BPM
        }
        return 0.0;
    }

    /**
     * 检查玩家是否在安全区
     */
    public static boolean isInSafeZone(PlayerEntity player) {
        // 检查周围是否有营火等安全光源
        // 简化实现：检查周围是否有足够的光照
        int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());
        return lightLevel >= 10;
    }

    /**
     * 获取心率恢复速度
     */
    public static double getHeartRateRecoverySpeed(PlayerEntity player) {
        return isInSafeZone(player) ? HEART_RATE_RECOVERY_SPEED_SAFE : HEART_RATE_RECOVERY_SPEED_UNSAFE;
    }

    /**
     * 获取心率上升速度
     */
    public static double getHeartRateRiseSpeed() {
        return HEART_RATE_RISE_SPEED;
    }

    /**
     * 更新玩家心率
     * 
     * @param player 目标玩家
     */
    public static void updatePlayerHeartRate(PlayerEntity player) {
        int newHeartRate = calculateHeartRate(player);
        HeartRateManager.forceHeartRate(player, newHeartRate);
    }

    /**
     * 获取心率状态描述
     */
    public static String getHeartRateStatus(int heartRate) {
        if (heartRate <= MAX_SAFE_HEART_RATE) {
            return "正常";
        } else if (heartRate <= DANGER_HEART_RATE) {
            return "警告";
        } else if (heartRate <= FATAL_HEART_RATE) {
            return "危险";
        } else {
            return "过载";
        }
    }

    /**
     * 检查是否接近猝死
     */
    public static boolean isNearFatal(int heartRate) {
        return heartRate >= FATAL_HEART_RATE - 10;
    }
}
