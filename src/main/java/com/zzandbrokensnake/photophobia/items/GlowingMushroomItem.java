package com.zzandbrokensnake.photophobia.items;

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

public class GlowingMushroomItem extends Item {
    private final MushroomType type;

    public GlowingMushroomItem(Settings settings, MushroomType type) {
        super(settings);
        this.type = type;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));

            // 根据蘑菇类型应用效果
            applyMushroomEffects(serverPlayer);

            // 消耗物品
            if (!serverPlayer.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return stack;
    }

    private void applyMushroomEffects(PlayerEntity player) {
        switch (type) {
            case BLUE:
                // 蓝光菇：基础食物，发光5分钟
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING, 6000, 0, false, false, true));
                player.getHungerManager().add(6, 0.6f);
                break;

            case GREEN:
                // 绿光菇：恢复效果，发光10分钟
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING, 12000, 0, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 200, 1, false, false, true));
                player.getHungerManager().add(8, 0.8f);
                break;

            case RED:
                // 红光菇：危险，强光但吸引怪物
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING, 12000, 2, false, false, true));
                // 增加威胁值
                com.zzandbrokensnake.photophobia.system.HeartRateManager.increaseThreat(player, 30);
                player.getHungerManager().add(4, 0.4f);
                break;

            case PURPLE:
                // 紫光菇：致幻，视觉扭曲
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NAUSEA, 400, 0, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS, 200, 0, false, false, true));
                player.getHungerManager().add(2, 0.2f);
                break;
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

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

    public enum MushroomType {
        BLUE, // 蓝光菇
        GREEN, // 绿光菇
        RED, // 红光菇
        PURPLE // 紫光菇
    }
}
