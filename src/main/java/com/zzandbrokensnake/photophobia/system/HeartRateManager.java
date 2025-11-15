package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static HeartRateData getHeartRateData(UUID playerUUID) {
        return playerHeartRates.computeIfAbsent(playerUUID, k -> new HeartRateData());
    }

    public static int getHeartRate(UUID playerUUID) {
        HeartRateData data = getHeartRateData(playerUUID);
        return data.getCurrentRate();
    }

    public static void forceHeartRate(UUID playerUUID, int rate) {
        HeartRateData data = getHeartRateData(playerUUID);
        data.setCurrentRate(rate);
    }

    /**
     * 计算玩家的当前心率 (v2.0优化版)
     * 
     * @param player 目标玩家
     * @return 计算后的心率值 (60-200)
     */
    public static int calculateHeartRate(PlayerEntity player) {
        // 基础静息心率：60-80 BPM (随机波动)
        int baseRate = 60 + player.getRandom().nextInt(21); // 60-80随机

        // 威胁检测 (大幅优化)
        double threatBonus = calculateThreatBonus(player);

        // 受伤加成
        double injuryBonus = calculateInjuryBonus(player);

        // 环境因素
        double environmentBonus = calculateEnvironmentBonus(player);

        // 战斗状态加成
        double combatBonus = calculateCombatBonus(player);

        int finalRate = (int) (baseRate + threatBonus + injuryBonus + environmentBonus + combatBonus);
        return MathHelper.clamp(finalRate, 60, 200);
    }

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

    private static double calculateInjuryBonus(PlayerEntity player) {
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float healthPercentage = currentHealth / maxHealth;

        // 血量越低，心率加成越高
        if (healthPercentage < 0.3f) {
            return 30.0; // 血量低于30%时显著增加心率
        } else if (healthPercentage < 0.6f) {
            return 15.0; // 血量低于60%时中等增加心率
        }

        return 0.0;
    }

    private static double calculateEnvironmentBonus(PlayerEntity player) {
        double bonus = 0.0;

        // 黑暗环境加成 (v2.0优化)
        if (player.getWorld().getLightLevel(player.getBlockPos()) < 7) {
            bonus += 3.0; // 每分钟+3 BPM的累积压力
        }

        // 高处加成
        if (player.getBlockPos().getY() > 100) {
            bonus += 5.0;
        }

        // 水下加成
        if (player.isSubmergedInWater()) {
            bonus += 8.0;
        }

        return bonus;
    }

    private static double calculateCombatBonus(PlayerEntity player) {
        double bonus = 0.0;

        // 攻击心率加成
        bonus += CombatTracker.calculateAttackHeartRateBonus(player);

        return bonus;
    }

    /**
     * 记录玩家攻击
     */
    public static void recordPlayerAttack(PlayerEntity player) {
        CombatTracker.recordAttack(player);
    }

    /**
     * 记录玩家击杀
     */
    public static void recordPlayerKill(PlayerEntity player) {
        // 击杀奖励：降低心率
        int currentRate = getHeartRate(player.getUuid());
        int newRate = Math.max(60, currentRate + CombatTracker.calculateKillHeartRateBonus());
        forceHeartRate(player.getUuid(), newRate);
    }

    /**
     * 记录成功格挡
     */
    public static void recordSuccessfulBlock(PlayerEntity player) {
        increaseThreat(player, CombatTracker.calculateBlockHeartRateBonus());
    }

    /**
     * 记录成功闪避
     */
    public static void recordSuccessfulDodge(PlayerEntity player) {
        increaseThreat(player, CombatTracker.calculateDodgeHeartRateBonus());
    }

    /**
     * 根据心率获取移动速度倍率 (v2.0)
     */
    public static float getMovementSpeedMultiplier(int heartRate) {
        if (heartRate < 100) {
            return 1.0f; // 正常速度
        } else if (heartRate < 140) {
            return 1.1f; // 速度+10% (肾上腺素)
        } else if (heartRate < 170) {
            return 1.0f; // 正常速度
        } else {
            return 0.8f; // 速度-20%，屏幕晃动
        }
    }

    /**
     * 获取心率阶段描述
     */
    public static HeartRateStage getHeartRateStage(int heartRate) {
        if (heartRate < 100) {
            return HeartRateStage.SAFE;
        } else if (heartRate < 140) {
            return HeartRateStage.WARNING;
        } else if (heartRate < 170) {
            return HeartRateStage.DANGER;
        } else {
            return HeartRateStage.FATAL;
        }
    }

    public enum HeartRateStage {
        SAFE("安全", 0x00FF00),
        WARNING("警戒", 0xFFFF00),
        DANGER("危险", 0xFFA500),
        FATAL("致命", 0xFF0000);

        private final String displayName;
        private final int color;

        HeartRateStage(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColor() {
            return color;
        }
    }

    /**
     * 更新玩家的心率数据
     */
    public static void updateHeartRate(PlayerEntity player) {
        UUID playerUUID = player.getUuid();
        HeartRateData data = getHeartRateData(playerUUID);

        // 每5秒更新一次心率
        long currentTime = player.getWorld().getTime();
        if (currentTime - data.lastUpdateTick > 100) { // 5秒 (100 ticks)
            int newRate = calculateHeartRate(player);
            data.setCurrentRate(newRate);
            data.lastUpdateTick = currentTime;

            // 检查是否过载
            if (newRate > 80 && !data.isOverloading()) {
                data.setOverloading(true);
                data.overloadStartTime = currentTime;
            } else if (newRate <= 80 && data.isOverloading()) {
                data.setOverloading(false);
            }
        }
    }

    /**
     * 增加威胁值
     */
    public static void increaseThreat(PlayerEntity player, int amount) {
        HeartRateData data = getHeartRateData(player.getUuid());
        int currentRate = data.getCurrentRate();
        data.setCurrentRate(Math.min(currentRate + amount, 100));
    }
}
