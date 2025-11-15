package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.*;

public class HeavyBlueEventSystem {
    private static final Map<UUID, Long> playerEventCooldowns = new HashMap<>();
    private static final Random random = new Random();

    // 事件类型枚举
    public enum EventType {
        HEART_RATE_SURGE("心率激增", 0xFF0000),
        VISUAL_DISTORTION("视觉扭曲", 0x0000FF),
        AUDITORY_HALLUCINATION("听觉幻觉", 0x00FF00),
        TEMPORAL_DISTORTION("时间扭曲", 0xFFFF00),
        REALITY_SHIFT("现实转换", 0xFF00FF);

        private final String displayName;
        private final int color;

        EventType(String displayName, int color) {
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
     * 更新重蓝事件系统
     */
    public static void updateHeavyBlueEvents(ServerWorld world) {
        long currentTime = world.getTime();

        // 每30秒检查一次事件触发
        if (currentTime % 600 == 0) { // 30秒
            for (ServerPlayerEntity player : world.getPlayers()) {
                tryTriggerEvent(player, world);
            }
        }
    }

    /**
     * 尝试触发重蓝事件
     */
    private static void tryTriggerEvent(ServerPlayerEntity player, ServerWorld world) {
        UUID playerUUID = player.getUuid();

        // 检查冷却时间
        long lastEventTime = playerEventCooldowns.getOrDefault(playerUUID, 0L);
        if (world.getTime() - lastEventTime < 1200) { // 60秒冷却
            return;
        }

        // 计算事件触发几率
        double triggerChance = calculateEventTriggerChance(player);

        if (random.nextDouble() < triggerChance) {
            triggerRandomEvent(player, world);
            playerEventCooldowns.put(playerUUID, world.getTime());
        }
    }

    /**
     * 计算事件触发几率
     */
    private static double calculateEventTriggerChance(PlayerEntity player) {
        double baseChance = 0.05; // 5%基础几率

        // 心率影响
        int heartRate = HeartRateManager.getHeartRate(player.getUuid());
        double heartRateBonus = (heartRate - 60) / 140.0 * 0.3; // 最高30%加成

        // 健康状态影响
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        double healthPenalty = (1.0 - healthPercentage) * 0.2; // 血量越低几率越高

        // 环境因素
        double environmentBonus = calculateEnvironmentEventBonus(player);

        return Math.min(baseChance + heartRateBonus + healthPenalty + environmentBonus, 0.8); // 最大80%
    }

    /**
     * 计算环境事件加成
     */
    private static double calculateEnvironmentEventBonus(PlayerEntity player) {
        double bonus = 0.0;

        // 黑暗环境加成
        if (player.getWorld().getLightLevel(player.getBlockPos()) < 7) {
            bonus += 0.1;
        }

        // 高处加成
        if (player.getBlockPos().getY() > 100) {
            bonus += 0.05;
        }

        // 水下加成
        if (player.isSubmergedInWater()) {
            bonus += 0.08;
        }

        // 附近有幽灵加成
        if (isGhostNearby(player)) {
            bonus += 0.15;
        }

        return bonus;
    }

    /**
     * 检查附近是否有幽灵
     */
    private static boolean isGhostNearby(PlayerEntity player) {
        return player.getWorld().getEntitiesByClass(
                com.zzandbrokensnake.photophobia.entity.GhostEntity.class,
                player.getBoundingBox().expand(16),
                ghost -> ghost.isAlive()).size() > 0;
    }

    /**
     * 触发随机事件
     */
    private static void triggerRandomEvent(ServerPlayerEntity player, ServerWorld world) {
        EventType[] eventTypes = EventType.values();
        EventType selectedEvent = eventTypes[random.nextInt(eventTypes.length)];

        switch (selectedEvent) {
            case HEART_RATE_SURGE:
                triggerHeartRateSurge(player, world);
                break;
            case VISUAL_DISTORTION:
                triggerVisualDistortion(player, world);
                break;
            case AUDITORY_HALLUCINATION:
                triggerAuditoryHallucination(player, world);
                break;
            case TEMPORAL_DISTORTION:
                triggerTemporalDistortion(player, world);
                break;
            case REALITY_SHIFT:
                triggerRealityShift(player, world);
                break;
        }

        // 发送事件通知
        player.sendMessage(
                Text.translatable("message.photophobia.heavy_blue_event")
                        .append(Text.literal(": " + selectedEvent.getDisplayName()))
                        .styled(style -> style.withColor(selectedEvent.getColor())),
                true);
    }

    /**
     * 心率激增事件
     */
    private static void triggerHeartRateSurge(ServerPlayerEntity player, ServerWorld world) {
        // 大幅增加心率
        int currentRate = HeartRateManager.getHeartRate(player.getUuid());
        int newRate = Math.min(currentRate + 50, 200);
        HeartRateManager.forceHeartRate(player.getUuid(), newRate);

        // 播放心跳音效
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                SoundCategory.AMBIENT, 1.0f, 0.8f);

        // 添加视觉效果
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.NAUSEA, 200, 1, false, false, true));
    }

    /**
     * 视觉扭曲事件
     */
    private static void triggerVisualDistortion(ServerPlayerEntity player, ServerWorld world) {
        // 添加多种视觉扭曲效果
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.BLINDNESS, 100, 0, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.NAUSEA, 300, 2, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.DARKNESS, 150, 0, false, false, true));

        // 播放扭曲音效
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
                SoundCategory.AMBIENT, 0.5f, 1.2f);
    }

    /**
     * 听觉幻觉事件
     */
    private static void triggerAuditoryHallucination(ServerPlayerEntity player, ServerWorld world) {
        // 播放多种幻觉音效
        SoundEvent[] hallucinationSounds = {
                SoundEvents.ENTITY_PHANTOM_AMBIENT,
                SoundEvents.ENTITY_WARDEN_AMBIENT,
                SoundEvents.ENTITY_ENDERMAN_STARE,
                SoundEvents.BLOCK_CONDUIT_AMBIENT
        };

        for (int i = 0; i < 3; i++) {
            world.playSound(null,
                    player.getBlockPos().add(random.nextInt(16) - 8, random.nextInt(8) - 4, random.nextInt(16) - 8),
                    hallucinationSounds[random.nextInt(hallucinationSounds.length)],
                    SoundCategory.AMBIENT, 0.3f, 0.5f + random.nextFloat() * 0.5f);
        }

        // 轻微增加心率
        HeartRateManager.increaseThreat(player, 15);
    }

    /**
     * 时间扭曲事件
     */
    private static void triggerTemporalDistortion(ServerPlayerEntity player, ServerWorld world) {
        // 随机移动玩家位置
        BlockPos currentPos = player.getBlockPos();
        BlockPos newPos = currentPos.add(
                random.nextInt(5) - 2,
                random.nextInt(3) - 1,
                random.nextInt(5) - 2);

        // 确保新位置安全
        if (world.getBlockState(newPos).isAir() && world.getBlockState(newPos.up()).isAir()) {
            player.teleport(newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5);
        }

        // 添加时间扭曲效果
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 100, 2, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.WEAKNESS, 100, 1, false, false, true));

        // 播放时间扭曲音效
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_PORTAL_TRAVEL,
                SoundCategory.AMBIENT, 0.7f, 0.3f);
    }

    /**
     * 现实转换事件
     */
    private static void triggerRealityShift(ServerPlayerEntity player, ServerWorld world) {
        // 大幅增加心率
        HeartRateManager.forceHeartRate(player.getUuid(), 180);

        // 添加多种负面效果
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.BLINDNESS, 80, 0, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.NAUSEA, 200, 3, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.WEAKNESS, 150, 2, false, false, true));
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE, 150, 1, false, false, true));

        // 播放现实转换音效
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN,
                SoundCategory.AMBIENT, 0.8f, 0.6f);

        // 生成粒子效果
        spawnRealityShiftParticles(player, world);
    }

    /**
     * 生成现实转换粒子效果
     */
    private static void spawnRealityShiftParticles(ServerPlayerEntity player, ServerWorld world) {
        // 在玩家周围生成粒子效果
        BlockPos pos = player.getBlockPos();
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + (random.nextDouble() - 0.5) * 4;
            double y = pos.getY() + random.nextDouble() * 2;
            double z = pos.getZ() + (random.nextDouble() - 0.5) * 4;

            // 发送粒子数据包（需要在客户端实现）
            // world.spawnParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    /**
     * 获取玩家事件冷却时间
     */
    public static long getPlayerEventCooldown(PlayerEntity player) {
        long lastEventTime = playerEventCooldowns.getOrDefault(player.getUuid(), 0L);
        return Math.max(0, 1200 - (player.getWorld().getTime() - lastEventTime));
    }

    /**
     * 重置玩家事件冷却时间（用于测试）
     */
    public static void resetPlayerCooldown(PlayerEntity player) {
        playerEventCooldowns.remove(player.getUuid());
    }

    /**
     * 强制触发重蓝事件（用于蓝之卵道具）
     */
    public static void triggerHeavyBlueEvent(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            triggerRandomEvent(serverPlayer, (ServerWorld) player.getWorld());
        }
    }
}
