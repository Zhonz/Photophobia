package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import java.util.*;

public class RewardSystem {
    private static final Map<UUID, PlayerRewardData> playerRewards = new HashMap<>();
    private static final Random random = new Random();

    // 奖励类型枚举
    public enum RewardType {
        HEART_RATE_REDUCTION("心率降低", 0x00FF00),
        CALMING_ITEM("镇静物品", 0x0088FF),
        TEMPORARY_BUFF("临时增益", 0xFFFF00),
        EXPERIENCE_BOOST("经验提升", 0x00FFFF),
        RARE_ITEM("稀有物品", 0xFF8800);

        private final String displayName;
        private final int color;

        RewardType(String displayName, int color) {
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

    // 玩家奖励数据
    public static class PlayerRewardData {
        private int survivalTime = 0;
        private int eventsSurvived = 0;
        private int ghostsDefeated = 0;
        private long lastRewardTime = 0;
        private int consecutiveSurvivalBonus = 0;

        public void incrementSurvivalTime() {
            survivalTime++;
        }

        public void incrementEventsSurvived() {
            eventsSurvived++;
        }

        public void incrementGhostsDefeated() {
            ghostsDefeated++;
        }

        public int getSurvivalTime() {
            return survivalTime;
        }

        public int getEventsSurvived() {
            return eventsSurvived;
        }

        public int getGhostsDefeated() {
            return ghostsDefeated;
        }

        public long getLastRewardTime() {
            return lastRewardTime;
        }

        public void setLastRewardTime(long time) {
            lastRewardTime = time;
        }

        public int getConsecutiveSurvivalBonus() {
            return consecutiveSurvivalBonus;
        }

        public void setConsecutiveSurvivalBonus(int bonus) {
            consecutiveSurvivalBonus = bonus;
        }

        public void setSurvivalTime(int time) {
            survivalTime = time;
        }

        public void setEventsSurvived(int events) {
            eventsSurvived = events;
        }

        public void setGhostsDefeated(int ghosts) {
            ghostsDefeated = ghosts;
        }
    }

    /**
     * 更新奖励系统
     */
    public static void updateRewardSystem(ServerWorld world) {
        long currentTime = world.getTime();

        // 每10秒检查一次奖励
        if (currentTime % 200 == 0) { // 10秒
            for (ServerPlayerEntity player : world.getPlayers()) {
                updatePlayerRewards(player, world);
            }
        }
    }

    /**
     * 更新玩家奖励
     */
    private static void updatePlayerRewards(ServerPlayerEntity player, ServerWorld world) {
        UUID playerUUID = player.getUuid();
        PlayerRewardData data = getPlayerRewardData(playerUUID);

        // 增加生存时间
        data.incrementSurvivalTime();

        // 检查是否应该给予奖励
        checkAndGiveRewards(player, data, world);
    }

    /**
     * 检查并给予奖励
     */
    private static void checkAndGiveRewards(ServerPlayerEntity player, PlayerRewardData data, ServerWorld world) {
        long currentTime = world.getTime();

        // 检查生存时间奖励
        if (data.getSurvivalTime() >= 120) { // 10分钟生存
            giveSurvivalReward(player, data, world);
            data.setSurvivalTime(0);
        }

        // 检查事件生存奖励
        if (data.getEventsSurvived() >= 3) { // 生存3个事件
            giveEventSurvivalReward(player, data, world);
            data.setConsecutiveSurvivalBonus(data.getConsecutiveSurvivalBonus() + 1);
        }

        // 检查幽灵击败奖励
        if (data.getGhostsDefeated() >= 1) { // 击败1个幽灵
            giveGhostDefeatReward(player, data, world);
            data.setGhostsDefeated(0);
        }
    }

    /**
     * 给予生存奖励
     */
    private static void giveSurvivalReward(ServerPlayerEntity player, PlayerRewardData data, ServerWorld world) {
        // 计算奖励等级
        int rewardLevel = calculateRewardLevel(player, data);

        // 随机选择奖励类型
        RewardType rewardType = selectRewardType(rewardLevel);

        // 给予奖励
        applyReward(player, rewardType, rewardLevel, world);

        // 发送奖励消息
        sendRewardMessage(player, rewardType, rewardLevel);

        // 更新最后奖励时间
        data.setLastRewardTime(world.getTime());
    }

    /**
     * 给予事件生存奖励
     */
    private static void giveEventSurvivalReward(ServerPlayerEntity player, PlayerRewardData data, ServerWorld world) {
        int consecutiveBonus = data.getConsecutiveSurvivalBonus();
        int rewardLevel = Math.min(3, 1 + consecutiveBonus);

        RewardType rewardType = RewardType.HEART_RATE_REDUCTION;
        applyReward(player, rewardType, rewardLevel, world);

        sendRewardMessage(player, rewardType, rewardLevel);
        data.setEventsSurvived(0);
    }

    /**
     * 给予幽灵击败奖励
     */
    private static void giveGhostDefeatReward(ServerPlayerEntity player, PlayerRewardData data, ServerWorld world) {
        RewardType rewardType = RewardType.RARE_ITEM;
        applyReward(player, rewardType, 2, world);

        sendRewardMessage(player, rewardType, 2);
    }

    /**
     * 计算奖励等级
     */
    private static int calculateRewardLevel(PlayerEntity player, PlayerRewardData data) {
        int level = 1;

        // 心率影响
        int heartRate = HeartRateManager.getHeartRate(player.getUuid());
        if (heartRate < 100) {
            level++; // 低心率奖励
        }

        // 健康状态影响
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        if (healthPercentage > 0.8f) {
            level++; // 高血量奖励
        }

        // 连续生存奖励
        level += Math.min(2, data.getConsecutiveSurvivalBonus());

        return Math.min(level, 5); // 最大5级奖励
    }

    /**
     * 选择奖励类型
     */
    private static RewardType selectRewardType(int rewardLevel) {
        RewardType[] allTypes = RewardType.values();

        // 根据奖励等级调整概率
        if (rewardLevel >= 4) {
            // 高等级奖励更可能获得稀有物品
            return random.nextFloat() < 0.6f ? RewardType.RARE_ITEM : allTypes[random.nextInt(allTypes.length)];
        } else if (rewardLevel >= 2) {
            // 中等等级奖励
            return allTypes[random.nextInt(allTypes.length)];
        } else {
            // 低等级奖励偏向基础奖励
            return random.nextFloat() < 0.7f ? RewardType.HEART_RATE_REDUCTION
                    : allTypes[random.nextInt(allTypes.length)];
        }
    }

    /**
     * 应用奖励
     */
    private static void applyReward(ServerPlayerEntity player, RewardType rewardType, int level, ServerWorld world) {
        switch (rewardType) {
            case HEART_RATE_REDUCTION:
                applyHeartRateReduction(player, level);
                break;
            case CALMING_ITEM:
                giveCalmingItem(player, level);
                break;
            case TEMPORARY_BUFF:
                applyTemporaryBuff(player, level);
                break;
            case EXPERIENCE_BOOST:
                giveExperienceBoost(player, level);
                break;
            case RARE_ITEM:
                giveRareItem(player, level);
                break;
        }

        // 播放奖励音效
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.AMBIENT, 0.8f, 1.0f);
    }

    /**
     * 应用心率降低奖励
     */
    private static void applyHeartRateReduction(ServerPlayerEntity player, int level) {
        int reduction = 10 + (level * 5); // 10-35 BPM降低
        int currentRate = HeartRateManager.getHeartRate(player.getUuid());
        int newRate = Math.max(60, currentRate - reduction);
        HeartRateManager.forceHeartRate(player.getUuid(), newRate);
    }

    /**
     * 给予镇静物品奖励
     */
    private static void giveCalmingItem(ServerPlayerEntity player, int level) {
        // 根据等级给予不同数量的镇静物品
        int count = 1 + (level - 1);

        // 随机选择镇静物品类型
        String[] calmingItems = { "calm_capsule", "calm_powder", "calm_spray" };
        String selectedItem = calmingItems[random.nextInt(calmingItems.length)];

        // 给予物品（需要实现物品给予逻辑）
        // player.giveItemStack(new ItemStack(Registry.ITEM.get(new
        // Identifier("photophobia", selectedItem)), count));
    }

    /**
     * 应用临时增益奖励
     */
    private static void applyTemporaryBuff(ServerPlayerEntity player, int level) {
        int duration = 600 + (level * 200); // 30-130秒

        // 根据等级给予不同增益
        if (level >= 3) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, duration, 1, false, false, true));
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.STRENGTH, duration, 0, false, false, true));
        } else if (level >= 2) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, duration, 1, false, false, true));
        } else {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, duration, 0, false, false, true));
        }
    }

    /**
     * 给予经验提升奖励
     */
    private static void giveExperienceBoost(ServerPlayerEntity player, int level) {
        int experience = 10 + (level * 5); // 10-35经验
        player.addExperience(experience);
    }

    /**
     * 给予稀有物品奖励
     */
    private static void giveRareItem(ServerPlayerEntity player, int level) {
        // 根据等级给予不同稀有物品
        // 需要实现稀有物品给予逻辑
        // player.giveItemStack(new ItemStack(Registry.ITEM.get(new
        // Identifier("photophobia", "rare_item")), level));
    }

    /**
     * 发送奖励消息
     */
    private static void sendRewardMessage(ServerPlayerEntity player, RewardType rewardType, int level) {
        String levelText = "";
        if (level > 1) {
            levelText = " (" + "★".repeat(level) + ")";
        }

        player.sendMessage(
                Text.translatable("message.photophobia.reward_received")
                        .append(Text.literal(": " + rewardType.getDisplayName() + levelText))
                        .styled(style -> style.withColor(rewardType.getColor())),
                true);
    }

    /**
     * 记录玩家生存事件
     */
    public static void recordEventSurvival(PlayerEntity player) {
        PlayerRewardData data = getPlayerRewardData(player.getUuid());
        data.incrementEventsSurvived();
    }

    /**
     * 记录幽灵击败
     */
    public static void recordGhostDefeat(PlayerEntity player) {
        PlayerRewardData data = getPlayerRewardData(player.getUuid());
        data.incrementGhostsDefeated();
    }

    /**
     * 获取玩家奖励数据
     */
    public static PlayerRewardData getPlayerRewardData(UUID playerUUID) {
        return playerRewards.computeIfAbsent(playerUUID, k -> new PlayerRewardData());
    }

    /**
     * 重置玩家奖励数据（用于测试）
     */
    public static void resetPlayerRewards(PlayerEntity player) {
        playerRewards.remove(player.getUuid());
    }
}
