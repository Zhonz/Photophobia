package com.zzandbrokensnake.photophobia.enchantments;

import com.zzandbrokensnake.photophobia.items.LanternItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class NightWatcherEnchantment extends Enchantment {

    public NightWatcherEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3; // 最大等级3
    }

    @Override
    public boolean isTreasure() {
        return true; // 宝藏附魔
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false; // 不在附魔台随机出现
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false; // 不在村民交易中出现
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        // 只能附魔在提灯上
        return stack.getItem() instanceof LanternItem;
    }

    /**
     * 获取附魔提供的额外光亮
     */
    public static int getExtraLightLevel(int level) {
        switch (level) {
            case 1:
                return 2; // 光亮从10提升到12
            case 2:
                return 3; // 光亮从10提升到13
            case 3:
                return 5; // 光亮从10提升到15
            default:
                return 0;
        }
    }

    /**
     * 获取附魔提供的耐久损耗减缓倍率
     */
    public static float getDurabilityMultiplier(int level) {
        switch (level) {
            case 1:
                return 0.8f; // 耐久损耗减缓20%
            case 2:
                return 0.6f; // 耐久损耗减缓40%
            case 3:
                return 0.4f; // 耐久损耗减缓60%
            default:
                return 1.0f;
        }
    }
}
