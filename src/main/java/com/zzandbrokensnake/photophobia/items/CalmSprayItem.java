package com.zzandbrokensnake.photophobia.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.system.HeartRateManager;

public class CalmSprayItem extends Item {
    private static final int MAX_USES = 10; // 最大使用次数

    public CalmSprayItem(Settings settings) {
        super(settings.maxDamage(MAX_USES));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && !world.isClient()) {
            // 减少70%超出正常范围的心率
            reduceHeartRate(player, 0.7f);

            // 消耗耐久度（除非在创造模式）
            if (!player.getAbilities().creativeMode) {
                stack.damage(1, player, (p) -> {
                    p.sendToolBreakStatus(user.getActiveHand());
                });
            }

            // 发送反馈消息
            player.sendMessage(net.minecraft.text.Text.literal("使用了安神喷雾，心率大幅降低"), true);
        }

        return stack;
    }

    /**
     * 减少超出正常范围的心率
     * 
     * @param player        玩家
     * @param reductionRate 减少比例 (0.0-1.0)
     */
    private void reduceHeartRate(PlayerEntity player, float reductionRate) {
        int currentHeartRate = HeartRateManager.getHeartRate(player.getUuid());
        int normalHeartRate = 80; // 正常心率范围上限 (根据HeartRateManager的定义)

        if (currentHeartRate > normalHeartRate) {
            int excessHeartRate = currentHeartRate - normalHeartRate;
            int reduction = (int) (excessHeartRate * reductionRate);
            int newHeartRate = currentHeartRate - reduction;

            // 设置新的心率，确保不低于正常范围
            HeartRateManager.forceHeartRate(player.getUuid(), Math.max(normalHeartRate, newHeartRate));
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20; // 快速使用
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK; // 使用喝的动作
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK; // 使用喝的声音
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // 检查耐久度
        if (stack.getDamage() < stack.getMaxDamage() || user.getAbilities().creativeMode) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        } else {
            user.sendMessage(net.minecraft.text.Text.literal("安神喷雾已用完"), true);
            return TypedActionResult.fail(stack);
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true; // 可以附魔
    }

    @Override
    public int getEnchantability() {
        return 15; // 中等附魔能力
    }

    /**
     * 获取剩余使用次数
     */
    public static int getRemainingUses(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage();
    }
}
