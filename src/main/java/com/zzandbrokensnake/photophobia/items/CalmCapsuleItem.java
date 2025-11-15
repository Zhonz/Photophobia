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

public class CalmCapsuleItem extends Item {

    public CalmCapsuleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && !world.isClient()) {
            // 减少50%超出正常范围的心率
            reduceHeartRate(player, 0.5f);

            // 消耗物品
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            // 发送反馈消息
            player.sendMessage(net.minecraft.text.Text.literal("使用了凝神胶囊，心率显著降低"), true);
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
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
