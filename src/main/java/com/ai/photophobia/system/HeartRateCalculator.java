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
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class HeartRateCalculator {

    /**
     * 计算玩家的当前心率
     * 
     * @param player 目标玩家
     * @return 计算后的心率值 (0-100)
     */
    public static int calculateHeartRate(PlayerEntity player) {
        int baseRate = 20 + player.getRandom().nextInt(21); // 20-40随机

        // 威胁检测
        double threatBonus = calculateThreatBonus(player);

        // 受伤加成
        double injuryBonus = calculateInjuryBonus(player);

        // 环境因素
        double environmentBonus = calculateEnvironmentBonus(player);

        int finalRate = (int) (baseRate + threatBonus + injuryBonus + environmentBonus);
        return MathHelper.clamp(finalRate, 0, 100);
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

            double distanceFactor = (16 - Math.sqrt(distance)) / 16.0;
            double threatFromEntity = distanceFactor * (isBehind ? 1.5 : 1.0) * 40;

            threat += threatFromEntity;
        }

        return threat;
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
        float healthPercentage = currentHealth / maxHealth;

        // 血量越低，心率越高
        if (healthPercentage < 0.3f) {
            return 30.0; // 严重受伤
        } else if (healthPercentage < 0.6f) {
            return 15.0; // 中等受伤
        } else if (healthPercentage < 0.8f) {
            return 5.0; // 轻微受伤
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

        // 黑暗环境加成
        int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());
        if (lightLevel < 4) {
            environmentBonus += 10.0; // 黑暗环境增加心率
        }

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
     * 更新玩家心率
     * 
     * @param player 目标玩家
     */
    public static void updatePlayerHeartRate(PlayerEntity player) {
        int newHeartRate = calculateHeartRate(player);
        HeartRateManager.forceHeartRate(player, newHeartRate);
    }
}
