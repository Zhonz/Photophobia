package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.List;

public class HeartRateCalculator {

    /**
     * 计算玩家的当前心率
     * 
     * @param player 目标玩家
     * @return 计算后的心率值 (60-180)
     */
    public static int calculateHeartRate(PlayerEntity player) {
        // 基础心率：正常成年人静息心率60-100，运动时可达180
        int baseRate = 60 + player.getRandom().nextInt(41); // 60-100随机

        // 威胁检测
        double threatBonus = calculateThreatBonus(player);

        // 受伤加成
        double injuryBonus = calculateInjuryBonus(player);

        // 环境因素
        double environmentBonus = calculateEnvironmentBonus(player);

        int finalRate = (int) (baseRate + threatBonus + injuryBonus + environmentBonus);
        return MathHelper.clamp(finalRate, 60, 180); // 限制在正常人类心率范围内
    }

    /**
     * 计算威胁加成
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
            double threatFromEntity = distanceFactor * (isBehind ? 1.5 : 1.0) * 60; // 调整为60以适应新范围

            threat += threatFromEntity;
        }

        return threat;
    }

    /**
     * 计算受伤加成
     */
    private static double calculateInjuryBonus(PlayerEntity player) {
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        if (healthPercentage < 0.3f) {
            return (1.0f - healthPercentage) * 30; // 血量低于30%时大幅增加
        } else if (healthPercentage < 0.6f) {
            return (1.0f - healthPercentage) * 15; // 血量低于60%时中等增加
        }
        return 0;
    }

    /**
     * 计算环境加成
     */
    private static double calculateEnvironmentBonus(PlayerEntity player) {
        double bonus = 0;

        // 光亮环境降低心率
        int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());
        if (lightLevel >= 10) {
            // 在明亮处心率降低
            bonus -= 15; // 显著降低心率
        } else if (lightLevel >= 7) {
            // 在中等亮度处心率略微降低
            bonus -= 8;
        } else if (lightLevel < 5) {
            // 在黑暗处心率增加
            bonus += 12;
        }

        // 高处加成
        if (player.getBlockPos().getY() > 100) {
            bonus += 5;
        }

        // 水下加成
        if (player.isSubmergedInWater()) {
            bonus += 8;
        }

        return bonus;
    }

    /**
     * 获取心率描述
     */
    public static String getHeartRateDescription(int heartRate) {
        if (heartRate < 70)
            return "平静";
        if (heartRate < 90)
            return "正常";
        if (heartRate < 120)
            return "紧张";
        if (heartRate < 150)
            return "恐慌";
        return "极度恐慌";
    }

    /**
     * 获取心率颜色
     */
    public static int getHeartRateColor(int heartRate) {
        if (heartRate < 70)
            return 0x00FF00; // 绿色
        if (heartRate < 90)
            return 0xFFFF00; // 黄色
        if (heartRate < 120)
            return 0xFFA500; // 橙色
        if (heartRate < 150)
            return 0xFF4500; // 红色
        return 0x8B0000; // 深红色
    }
}
