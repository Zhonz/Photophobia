package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;

public class PlayerLanternSystem {

    /**
     * 给予玩家首次提灯
     */
    public static void giveFirstLantern(ServerPlayerEntity player) {
        if (player == null)
            return;

        // 检查玩家是否已经有提灯
        if (!hasLantern(player)) {
            giveLanternToPlayer(player);
        }
    }

    /**
     * 给予玩家提灯
     */
    private static void giveLanternToPlayer(ServerPlayerEntity player) {
        ItemStack lanternStack = new ItemStack(ItemRegistry.LANTERN);

        // 设置提灯为未点亮状态
        lanternStack.getOrCreateNbt().putBoolean("Lit", false);

        // 尝试将提灯放入玩家背包
        if (!player.getInventory().insertStack(lanternStack)) {
            // 如果背包已满，直接给予玩家
            player.giveItemStack(lanternStack);
        }

        // 发送提示消息
        player.sendMessage(net.minecraft.text.Text.literal("你获得了一个提灯，在黑暗中它将保护你"), false);
    }

    /**
     * 检查玩家是否拥有提灯
     */
    public static boolean hasLantern(PlayerEntity player) {
        if (player == null)
            return false;

        // 检查主手、副手和背包
        return player.getMainHandStack().getItem() == ItemRegistry.LANTERN ||
                player.getOffHandStack().getItem() == ItemRegistry.LANTERN ||
                player.getInventory().containsAny(stack -> stack.getItem() == ItemRegistry.LANTERN);
    }

    /**
     * 获取玩家的提灯（如果存在）
     */
    public static ItemStack getPlayerLantern(PlayerEntity player) {
        if (player == null)
            return ItemStack.EMPTY;

        // 检查主手
        if (player.getMainHandStack().getItem() == ItemRegistry.LANTERN) {
            return player.getMainHandStack();
        }

        // 检查副手
        if (player.getOffHandStack().getItem() == ItemRegistry.LANTERN) {
            return player.getOffHandStack();
        }

        // 检查背包
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == ItemRegistry.LANTERN) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
