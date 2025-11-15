package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTracker {
    private static final Map<UUID, CombatData> playerCombatData = new HashMap<>();

    public static class CombatData {
        private int attackCount = 0;
        private long lastAttackTime = 0;
        private long combatStartTime = 0;
        private boolean inCombat = false;

        public int getAttackCount() {
            return attackCount;
        }

        public void incrementAttackCount() {
            this.attackCount++;
        }

        public void resetAttackCount() {
            this.attackCount = 0;
        }

        public long getLastAttackTime() {
            return lastAttackTime;
        }

        public void setLastAttackTime(long time) {
            this.lastAttackTime = time;
        }

        public boolean isInCombat() {
            return inCombat;
        }

        public void setInCombat(boolean inCombat) {
            this.inCombat = inCombat;
        }

        public long getCombatStartTime() {
            return combatStartTime;
        }

        public void setCombatStartTime(long time) {
            this.combatStartTime = time;
        }
    }

    public static CombatData getCombatData(UUID playerUUID) {
        return playerCombatData.computeIfAbsent(playerUUID, k -> new CombatData());
    }

    /**
     * 记录玩家攻击
     */
    public static void recordAttack(PlayerEntity player) {
        CombatData data = getCombatData(player.getUuid());
        data.incrementAttackCount();
        data.setLastAttackTime(player.getWorld().getTime());
        data.setInCombat(true);

        // 如果这是第一次攻击，记录战斗开始时间
        if (data.getAttackCount() == 1) {
            data.setCombatStartTime(player.getWorld().getTime());
        }

        // 检查连续战斗惩罚
        checkCombatFatigue(player, data);
    }

    /**
     * 检查连续战斗惩罚
     */
    private static void checkCombatFatigue(PlayerEntity player, CombatData data) {
        long currentTime = player.getWorld().getTime();
        long combatDuration = currentTime - data.getCombatStartTime();

        // 30秒内攻击超过5次触发力竭debuff
        if (combatDuration < 600 && data.getAttackCount() > 5) {
            // 这里可以添加力竭效果
            // 心率恢复-50%，持续30秒
            HeartRateManager.increaseThreat(player, 20);
        }
    }

    /**
     * 计算攻击心率加成
     */
    public static int calculateAttackHeartRateBonus(PlayerEntity player) {
        CombatData data = getCombatData(player.getUuid());
        long currentTime = player.getWorld().getTime();
        long timeSinceLastAttack = currentTime - data.getLastAttackTime();

        // 最近5秒内攻击过，增加心率
        if (timeSinceLastAttack < 100) {
            return 8; // 每次攻击+8 BPM
        }

        return 0;
    }

    /**
     * 计算击杀心率奖励
     */
    public static int calculateKillHeartRateBonus() {
        return -15; // 击杀怪物-15 BPM
    }

    /**
     * 更新战斗状态
     */
    public static void updateCombatState(PlayerEntity player) {
        CombatData data = getCombatData(player.getUuid());
        long currentTime = player.getWorld().getTime();
        long timeSinceLastAttack = currentTime - data.getLastAttackTime();

        // 10秒内没有攻击，退出战斗状态
        if (timeSinceLastAttack > 200 && data.isInCombat()) {
            data.setInCombat(false);
            data.resetAttackCount();

            // 成功撤退奖励
            if (HeartRateManager.getHeartRate(player.getUuid()) > 140) {
                // 获得"喘息"buff效果
                // 下次心率上升-30%，持续60秒
                // 这里可以添加状态效果
            }
        }
    }

    /**
     * 计算成功格挡心率加成
     */
    public static int calculateBlockHeartRateBonus() {
        return 5; // 成功格挡+5 BPM
    }

    /**
     * 计算成功闪避心率加成
     */
    public static int calculateDodgeHeartRateBonus() {
        return 10; // 成功闪避+10 BPM
    }
}
