package com.ai.photophobia.items;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 荧光蘑菇物品 - 提供发光效果的基础食物
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class GlowingMushroomItem extends Item {

    private final MushroomType type;

    public GlowingMushroomItem(Settings settings, MushroomType type) {
        super(settings);
        this.type = type;
    }

    public enum MushroomType {
        BLUE("blue", 5 * 60 * 20, 0.2f), // 蓝光菇：5分钟发光，降低心率上升20%
        GREEN("green", 10 * 60 * 20, 0.3f), // 绿光菇：10分钟发光，降低心率上升30%
        RED("red", 3 * 60 * 20, -0.5f), // 红光菇：3分钟强光，但增加心率上升50%
        PURPLE("purple", 2 * 60 * 20, 0.1f); // 紫光菇：2分钟发光，视觉扭曲

        private final String name;
        private final int duration; // 持续时间 (ticks)
        private final float heartRateModifier; // 心率变化修正

        MushroomType(String name, int duration, float heartRateModifier) {
            this.name = name;
            this.duration = duration;
            this.heartRateModifier = heartRateModifier;
        }

        public String getName() {
            return name;
        }

        public int getDuration() {
            return duration;
        }

        public float getHeartRateModifier() {
            return heartRateModifier;
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));

            // 应用蘑菇效果
            applyMushroomEffects(serverPlayer);

            // 消耗物品
            if (!serverPlayer.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    // 在Minecraft 1.21.1中，食用时间由默认值控制
    // 不需要重写getMaxUseTime方法

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    /**
     * 应用蘑菇效果
     */
    private void applyMushroomEffects(ServerPlayerEntity player) {
        // 基础发光效果
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.GLOWING,
                type.getDuration(),
                0,
                false,
                true));

        // 根据蘑菇类型应用特殊效果
        switch (type) {
            case BLUE:
                // 蓝光菇：基础发光 + 心率保护
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        100, // 5秒
                        0,
                        false,
                        true));
                player.sendMessage(net.minecraft.text.Text.literal("你感到平静，心率上升速度降低了"), true);
                break;

            case GREEN:
                // 绿光菇：更强发光 + 恢复效果
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        200, // 10秒
                        1,
                        false,
                        true));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION,
                        1200, // 1分钟
                        0,
                        false,
                        true));
                player.sendMessage(net.minecraft.text.Text.literal("你感到充满活力，恢复效果增强了"), true);
                break;

            case RED:
                // 红光菇：强光但危险
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING,
                        type.getDuration(),
                        1, // 更强的发光
                        false,
                        true));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH,
                        200, // 10秒
                        0,
                        false,
                        true));
                player.sendMessage(net.minecraft.text.Text.literal("危险的光芒！怪物会被强烈吸引"), true);
                break;

            case PURPLE:
                // 紫光菇：致幻效果
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NAUSEA,
                        200, // 10秒
                        0,
                        false,
                        true));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS,
                        100, // 5秒
                        0,
                        false,
                        true));
                player.sendMessage(net.minecraft.text.Text.literal("视野扭曲...你看到了奇怪的东西"), true);
                break;
        }

        // 应用心率修正效果
        applyHeartRateModifier(player);
    }

    /**
     * 应用心率修正
     */
    private void applyHeartRateModifier(ServerPlayerEntity player) {
        // TODO: 实现心率修正效果
        // 这里可以添加自定义状态效果来修改心率计算
    }

    /**
     * 获取蘑菇类型
     */
    public MushroomType getMushroomType() {
        return type;
    }

    /**
     * 获取蘑菇的发光范围
     */
    public int getLightLevel() {
        switch (type) {
            case BLUE:
                return 5; // 5格范围
            case GREEN:
                return 7; // 7格范围
            case RED:
                return 10; // 10格范围（危险）
            case PURPLE:
                return 4; // 4格范围
            default:
                return 5;
        }
    }

    /**
     * 检查是否会吸引怪物
     */
    public boolean attractsMonsters() {
        return type == MushroomType.RED; // 只有红光菇会强烈吸引怪物
    }
}
