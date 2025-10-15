package com.ai.photophobia.items;

import com.ai.photophobia.system.HeartRateManager;
import com.ai.photophobia.system.PlayerStatusManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * 凝神胶囊 - 回复30%压力值，可使用10次
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class CalmCapsuleItem extends Item {
    private static final int MAX_USES = 10;
    private static final float STRESS_REDUCTION = 0.30f; // 30%压力值回复

    public CalmCapsuleItem(Settings settings) {
        super(settings.maxDamage(MAX_USES));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && user instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            // 检查是否可以使用
            if (stack.getDamage() >= stack.getMaxDamage()) {
                return TypedActionResult.fail(stack);
            }

            // 应用宁神效果
            applyCalmEffect(serverPlayer);

            // 消耗耐久度
            stack.setDamage(stack.getDamage() + 1);

            // 播放使用音效
            world.playSound(
                    null,
                    user.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EAT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f);

            // 显示使用效果
            user.sendMessage(net.minecraft.text.Text.literal("§a使用凝神胶囊，压力值降低30%"), true);

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    /**
     * 应用宁神效果
     */
    private void applyCalmEffect(net.minecraft.server.network.ServerPlayerEntity player) {
        // 降低心率（压力值）
        int currentHeartRate = HeartRateManager.getHeartRate(player);
        int reducedHeartRate = (int) (currentHeartRate * (1.0f - STRESS_REDUCTION));

        // 设置宁神效果
        PlayerStatusManager.applyCalmEffect(player, 6000); // 5分钟效果

        // 强制降低心率
        HeartRateManager.forceHeartRate(player, Math.max(0, reducedHeartRate));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getDamage() < stack.getMaxDamage();
    }

    @Override
    public int getEnchantability() {
        return 0;
    }
}
