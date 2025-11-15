package com.zzandbrokensnake.photophobia.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.system.HeavyBlueEventSystem;

public class BlueEggItem extends Item {

    public BlueEggItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // 播放使用音效
            world.playSound(
                    null,
                    user.getBlockPos(),
                    SoundEvents.ENTITY_ENDER_EYE_DEATH,
                    SoundCategory.PLAYERS,
                    1.0f,
                    0.8f);

            // 触发重蓝事件
            HeavyBlueEventSystem.triggerHeavyBlueEvent(user);

            // 消耗物品
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            // 播放粒子效果
            // 这里可以添加自定义粒子效果

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    /**
     * 获取蓝之卵的描述信息
     */
    public static String getDescription() {
        return """
                蓝之卵 - 重蓝之门的钥匙

                使用效果:
                - 立即触发重蓝事件
                - 消耗品 (创造模式除外)
                - 播放特殊音效和粒子效果

                获取方式:
                - 在平衡之塔中找到
                - 击败特殊幽灵掉落
                - 稀有战利品箱
                """;
    }
}
