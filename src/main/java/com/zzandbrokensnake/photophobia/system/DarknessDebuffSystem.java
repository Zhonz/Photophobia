package com.zzandbrokensnake.photophobia.system;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import com.zzandbrokensnake.photophobia.items.LanternItem;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;

/**
 * 黑暗debuff系统
 * 在不使用提灯的情况下，持续给予玩家黑暗debuff
 * 在提灯被点亮时立刻失效
 */
public class DarknessDebuffSystem {

    private static final int DARKNESS_DURATION = 100; // 5秒持续时间（20 ticks = 1秒）
    private static final int DARKNESS_AMPLIFIER = 0; // 黑暗效果等级

    public static void initialize() {
        // 监听服务器tick事件
        ServerTickEvents.END_WORLD_TICK.register(DarknessDebuffSystem::onWorldTick);
    }

    /**
     * 世界tick事件处理
     */
    private static void onWorldTick(ServerWorld world) {
        // 每tick检查所有玩家
        for (ServerPlayerEntity player : world.getPlayers()) {
            handlePlayerDarkness(player);
        }
    }

    /**
     * 处理玩家的黑暗debuff
     */
    private static void handlePlayerDarkness(ServerPlayerEntity player) {
        // 检查玩家是否持有点亮的提灯
        boolean hasLitLantern = hasLitLanternInHand(player);

        if (hasLitLantern) {
            // 如果持有点亮的提灯，移除黑暗debuff
            removeDarknessDebuff(player);
        } else {
            // 如果没有点亮的提灯，给予黑暗debuff
            applyDarknessDebuff(player);
        }
    }

    /**
     * 检查玩家是否在主手或副手持有点亮的提灯
     */
    private static boolean hasLitLanternInHand(PlayerEntity player) {
        // 检查主手
        ItemStack mainHandStack = player.getMainHandStack();
        if (isLitLantern(mainHandStack)) {
            return true;
        }

        // 检查副手
        ItemStack offHandStack = player.getOffHandStack();
        if (isLitLantern(offHandStack)) {
            return true;
        }

        return false;
    }

    /**
     * 检查物品是否为点亮的提灯
     */
    private static boolean isLitLantern(ItemStack stack) {
        if (stack.getItem() instanceof LanternItem) {
            // 检查提灯是否点亮
            return stack.getOrCreateNbt().getBoolean("Lit");
        }
        return false;
    }

    /**
     * 给予玩家黑暗debuff
     */
    private static void applyDarknessDebuff(PlayerEntity player) {
        // 检查玩家是否已经有黑暗效果
        if (!player.hasStatusEffect(StatusEffects.DARKNESS)) {
            // 给予黑暗效果
            StatusEffectInstance darknessEffect = new StatusEffectInstance(
                    StatusEffects.DARKNESS,
                    DARKNESS_DURATION,
                    DARKNESS_AMPLIFIER,
                    false, // 环境效果
                    true, // 显示粒子
                    true // 显示图标
            );
            player.addStatusEffect(darknessEffect);
        } else {
            // 如果已经有黑暗效果，刷新持续时间
            StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.DARKNESS);
            if (currentEffect != null && currentEffect.getDuration() < 20) {
                // 如果效果即将结束，刷新持续时间
                StatusEffectInstance refreshedEffect = new StatusEffectInstance(
                        StatusEffects.DARKNESS,
                        DARKNESS_DURATION,
                        DARKNESS_AMPLIFIER,
                        false,
                        true,
                        true);
                player.addStatusEffect(refreshedEffect);
            }
        }
    }

    /**
     * 移除玩家的黑暗debuff
     */
    private static void removeDarknessDebuff(PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.DARKNESS)) {
            player.removeStatusEffect(StatusEffects.DARKNESS);
        }
    }

    /**
     * 检查玩家是否应该自动熄灭提灯
     * 当提灯不在主手或副手时自动熄灭
     */
    public static void checkLanternAutoExtinguish(PlayerEntity player) {
        // 检查玩家背包中的所有提灯
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof LanternItem) {
                // 检查提灯是否点亮
                if (stack.getOrCreateNbt().getBoolean("Lit")) {
                    // 检查提灯是否在主手或副手
                    boolean inMainHand = player.getMainHandStack() == stack;
                    boolean inOffHand = player.getOffHandStack() == stack;

                    if (!inMainHand && !inOffHand) {
                        // 提灯不在手持位置，自动熄灭
                        stack.getOrCreateNbt().putBoolean("Lit", false);
                        stack.getOrCreateNbt().putInt("CustomModelData", 0);

                        if (player instanceof ServerPlayerEntity serverPlayer) {
                            serverPlayer.sendMessage(net.minecraft.text.Text.literal("提灯已自动熄灭"), true);
                        }
                    }
                }
            }
        }
    }
}
