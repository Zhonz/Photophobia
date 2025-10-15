package com.ai.photophobia.system;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 深蓝事件管理器
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class DeepBlueEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("畏光-深蓝事件");

    // 事件状态
    private static boolean isActive = false;
    private static long startTime = 0;
    private static final long DURATION = 36000; // 3游戏日（tick）

    /**
     * 开始深蓝事件
     */
    public static void startEvent(ServerWorld world) {
        if (isActive) {
            LOGGER.warn("深蓝事件已经在进行中");
            return;
        }

        isActive = true;
        startTime = world.getTime();

        LOGGER.info("深蓝事件开始，将持续 {} 游戏日", DURATION / 12000);

        // 环境效果
        changeSkyColor();
        playAlertSound(world);

        // 通知所有玩家
        world.getPlayers().forEach(player -> {
            player.sendMessage(Text.literal("§9深蓝事件已触发！天空变为深蓝色，持续3个游戏日。"), false);
            player.sendMessage(Text.literal("§c警告：怪物生成规则已改变，请做好准备！"), false);
        });

        // 更新怪物生成规则
        updateMobSpawningRules();
    }

    /**
     * 结束深蓝事件
     */
    public static void endEvent(ServerWorld world) {
        if (!isActive) {
            return;
        }

        isActive = false;

        LOGGER.info("深蓝事件结束");

        // 通知所有玩家
        world.getPlayers().forEach(player -> {
            player.sendMessage(Text.literal("§a深蓝事件已结束。天空恢复正常。"), false);
        });

        // 恢复正常的怪物生成规则
        restoreNormalSpawningRules();

        // 给予奖励
        world.getPlayers().forEach(DeepBlueRewards::giveRewards);
    }

    /**
     * 事件检查
     */
    public static void tick(ServerWorld world) {
        if (isActive && world.getTime() - startTime >= DURATION) {
            endEvent(world);
        }
    }

    /**
     * 改变天空颜色
     */
    private static void changeSkyColor() {
        // 在1.20.1中，天空颜色改变需要通过客户端同步实现
        // 这里先记录日志，实际实现需要网络同步
        LOGGER.info("天空颜色变为深蓝色");
    }

    /**
     * 播放警报音效
     */
    private static void playAlertSound(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            // 播放类似不死图腾的音效
            world.playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ITEM_TOTEM_USE,
                    SoundCategory.AMBIENT,
                    1.0f,
                    0.8f);
        });
    }

    /**
     * 更新怪物生成规则
     */
    private static void updateMobSpawningRules() {
        // 在深蓝期间，禁用诡异类生物，增强Boss和小兵生成
        LOGGER.info("更新怪物生成规则：禁用诡异类，增强Boss和小兵生成");

        // 具体实现需要与怪物生成系统集成
        // 这里先记录日志
    }

    /**
     * 恢复正常的怪物生成规则
     */
    private static void restoreNormalSpawningRules() {
        LOGGER.info("恢复正常的怪物生成规则");

        // 恢复正常的生成规则
        // 具体实现需要与怪物生成系统集成
    }

    /**
     * 检查事件是否活跃
     */
    public static boolean isActive() {
        return isActive;
    }

    /**
     * 获取事件剩余时间（tick）
     */
    public static long getRemainingTime(ServerWorld world) {
        if (!isActive) {
            return 0;
        }
        return Math.max(0, DURATION - (world.getTime() - startTime));
    }

    /**
     * 获取事件进度（0.0-1.0）
     */
    public static float getProgress(ServerWorld world) {
        if (!isActive) {
            return 0.0f;
        }
        return Math.min(1.0f, (float) (world.getTime() - startTime) / DURATION);
    }
}
