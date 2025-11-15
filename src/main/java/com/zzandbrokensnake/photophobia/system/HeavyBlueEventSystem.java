package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.*;

/**
 * 重蓝事件系统 - 为后续构思的新效果预留框架
 * 
 * 当前版本已清空所有具体效果实现，等待新的构思效果设计
 */
public class HeavyBlueEventSystem {
    private static final Map<UUID, Long> playerEventCooldowns = new HashMap<>();
    private static final Random random = new Random();

    /**
     * 更新重蓝事件系统
     */
    public static void updateHeavyBlueEvents(ServerWorld world) {
        // 预留更新逻辑，等待新效果设计
    }

    /**
     * 强制触发重蓝事件（用于蓝之卵道具）
     */
    public static void triggerHeavyBlueEvent(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // 预留触发逻辑，等待新效果设计
            // 这里可以添加新的构思效果
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
}
