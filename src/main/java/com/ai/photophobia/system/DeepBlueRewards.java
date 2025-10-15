package com.ai.photophobia.system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 深蓝事件奖励系统
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class DeepBlueRewards {
    private static final Random random = Random.create();

    /**
     * 给予玩家深蓝事件奖励
     */
    public static void giveRewards(PlayerEntity player) {
        if (player == null || player.getWorld().isClient()) {
            return;
        }

        // 随机资源池
        List<ItemStack> rewardPool = Arrays.asList(
                new ItemStack(Items.DIAMOND, random.nextInt(3) + 1),
                new ItemStack(Items.EMERALD, random.nextInt(5) + 3),
                new ItemStack(Items.GOLD_INGOT, random.nextInt(8) + 5),
                new ItemStack(Items.IRON_INGOT, random.nextInt(12) + 8),
                new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextInt(3) + 1));

        // 随机选择3-5种奖励
        Collections.shuffle(rewardPool);
        int rewardCount = random.nextInt(3) + 3; // 3-5种奖励

        player.sendMessage(net.minecraft.text.Text.literal("§6深蓝事件奖励："), false);

        for (int i = 0; i < Math.min(rewardCount, rewardPool.size()); i++) {
            ItemStack reward = rewardPool.get(i).copy();
            player.giveItemStack(reward);

            // 显示获得的奖励
            player.sendMessage(net.minecraft.text.Text.literal("§a+ " + reward.getCount() + "x " +
                    getItemDisplayName(reward)), false);
        }

        // 额外经验奖励
        int expReward = random.nextInt(20) + 10; // 10-30经验
        player.addExperience(expReward);
        player.sendMessage(net.minecraft.text.Text.literal("§e+ " + expReward + " 经验"), false);
    }

    /**
     * 获取物品显示名称
     */
    private static String getItemDisplayName(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND) {
            return "钻石";
        } else if (stack.getItem() == Items.EMERALD) {
            return "绿宝石";
        } else if (stack.getItem() == Items.GOLD_INGOT) {
            return "金锭";
        } else if (stack.getItem() == Items.IRON_INGOT) {
            return "铁锭";
        } else if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
            return "经验瓶";
        } else {
            return stack.getName().getString();
        }
    }

    /**
     * 获取随机奖励（用于测试）
     */
    public static ItemStack getRandomReward() {
        List<ItemStack> rewardPool = Arrays.asList(
                new ItemStack(Items.DIAMOND, random.nextInt(3) + 1),
                new ItemStack(Items.EMERALD, random.nextInt(5) + 3),
                new ItemStack(Items.GOLD_INGOT, random.nextInt(8) + 5),
                new ItemStack(Items.IRON_INGOT, random.nextInt(12) + 8),
                new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextInt(3) + 1));

        Collections.shuffle(rewardPool);
        return rewardPool.get(0).copy();
    }
}
