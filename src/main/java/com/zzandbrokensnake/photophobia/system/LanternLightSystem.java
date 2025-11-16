package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.enchantment.EnchantmentHelper;
import com.zzandbrokensnake.photophobia.items.LanternItem;
import com.zzandbrokensnake.photophobia.enchantments.NightWatcherEnchantment;
import com.zzandbrokensnake.photophobia.registry.EnchantmentRegistry;

public class LanternLightSystem {

    /**
     * 为提灯提供动态光照
     */
    public static void provideLightForLantern(PlayerEntity player, ItemStack lanternStack) {
        if (player == null || player.getWorld() == null || lanternStack.isEmpty()) {
            return;
        }

        World world = player.getWorld();

        // 检查提灯是否点亮
        if (isLanternLit(lanternStack)) {
            // 计算光照等级（考虑附魔）
            int lightLevel = calculateLightLevel(lanternStack);

            // 在玩家位置创建光照
            BlockPos playerPos = player.getBlockPos();
            createDynamicLight(world, playerPos, lightLevel);
        }
    }

    /**
     * 检查提灯是否点亮
     */
    private static boolean isLanternLit(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("Lit");
    }

    /**
     * 计算光照等级
     */
    private static int calculateLightLevel(ItemStack stack) {
        int baseLightLevel = 10; // 基础光照等级

        // 检查巡夜者附魔等级
        int nightWatcherLevel = EnchantmentHelper.getLevel(EnchantmentRegistry.NIGHT_WATCHER, stack);

        // 根据附魔等级增加光照
        int extraLight = NightWatcherEnchantment.getExtraLightLevel(nightWatcherLevel);

        return Math.min(15, baseLightLevel + extraLight); // 最大光照等级15
    }

    /**
     * 创建动态光照
     */
    private static void createDynamicLight(World world, BlockPos pos, int lightLevel) {
        // 简化的光照实现：在玩家位置放置临时光源
        // 注意：这是一个简化版本，实际游戏中可能需要更复杂的光照系统

        // 检查当前位置是否已经有光源
        if (world.getBlockState(pos).getLuminance() < lightLevel) {
            // 在实际实现中，这里应该使用动态光源API
            // 临时：记录光照信息（用于调试）
            // System.out.println("提灯光照: 位置=" + pos + ", 等级=" + lightLevel);

            // 这里可以添加实际的光照逻辑
            // 例如：在玩家周围创建光照效果
            createLightEffect(world, pos, lightLevel);
        }
    }

    /**
     * 创建光照效果
     */
    private static void createLightEffect(World world, BlockPos pos, int lightLevel) {
        // 在实际实现中，这里应该：
        // 1. 使用Fabric的动态光源API创建移动光源
        // 2. 或者使用粒子效果模拟光照
        // 3. 或者修改周围方块的光照等级

        // 临时实现：在控制台输出光照信息
        if (world.isClient) {
            // 客户端：可以添加视觉特效
            // 例如：粒子效果、屏幕着色器等
        } else {
            // 服务器端：可以记录光照信息
        }
    }

    /**
     * 移除提灯光照
     */
    public static void removeLanternLight(PlayerEntity player) {
        // 在实际实现中，移除玩家的动态光源
        if (player != null && player.getWorld() != null) {
            BlockPos playerPos = player.getBlockPos();
            // 移除光照
            removeLightEffect(player.getWorld(), playerPos);
        }
    }

    /**
     * 移除光照效果
     */
    private static void removeLightEffect(World world, BlockPos pos) {
        // 在实际实现中，移除动态光源
        // 临时实现
    }
}
