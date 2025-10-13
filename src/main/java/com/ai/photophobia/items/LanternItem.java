package com.ai.photophobia.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * 提灯物品 - 提供光源但会吸引怪物
 * 
 * @author AI Developer
 * @version 1.0
 * @since 2024
 */
public class LanternItem extends Item {
    private static final String FUEL_KEY = "Fuel";

    public LanternItem(Settings settings) {
        super(settings.maxDamage(100));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // 检查是否激活
            if (isLit(stack) && (selected || isInOffhand(player, stack))) {
                // 消耗燃料
                if (world.getTime() % 20 == 0) { // 每秒
                    consumeFuel(stack, player);
                }

                // 增加威胁值
                increaseThreatLevel(player, 20);

                // 吸引趋光性怪物
                attractLightSeekingMobs(player);
            }
        }
    }

    /*
     * @Override
     * public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand
     * hand) {
     * ItemStack stack = user.getStackInHand(hand);
     * 
     * if (!world.isClient) {
     * toggleLantern(stack, user);
     * }
     * 
     * return TypedActionResult.success(stack);
     * }
     */

    /**
     * 检查提灯是否点亮
     * 
     * @param stack 物品堆栈
     * @return 是否点亮
     */
    private boolean isLit(ItemStack stack) {
        return stack.getDamage() < stack.getMaxDamage();
    }

    /**
     * 检查物品是否在副手
     * 
     * @param player 玩家
     * @param stack  物品堆栈
     * @return 是否在副手
     */
    private boolean isInOffhand(PlayerEntity player, ItemStack stack) {
        return player.getOffHandStack() == stack;
    }

    /**
     * 消耗燃料
     * 
     * @param stack  物品堆栈
     * @param player 玩家
     */
    private void consumeFuel(ItemStack stack, PlayerEntity player) {
        int currentDamage = stack.getDamage();
        if (currentDamage < stack.getMaxDamage()) {
            stack.setDamage(currentDamage + 1);
        } else {
            // 燃料耗尽
            player.sendMessage(net.minecraft.text.Text.literal("提灯燃料已耗尽！"), true);
        }
    }

    /**
     * 增加威胁等级
     * 
     * @param player 玩家
     * @param amount 增加量
     */
    private void increaseThreatLevel(PlayerEntity player, int amount) {
        // 这里可以连接到威胁系统
        // 暂时只记录日志
        if (player.getWorld().getTime() % 100 == 0) {
            System.out.println("提灯增加了威胁等级: " + amount);
        }
    }

    /**
     * 吸引趋光性怪物
     * 
     * @param player 玩家
     */
    private void attractLightSeekingMobs(PlayerEntity player) {
        // 这里可以连接到怪物AI系统
        // 暂时只记录日志
        if (player.getWorld().getTime() % 200 == 0) {
            System.out.println("提灯吸引了趋光性怪物");
        }
    }

    /**
     * 切换提灯状态
     * 
     * @param stack  物品堆栈
     * @param player 玩家
     */
    private void toggleLantern(ItemStack stack, PlayerEntity player) {
        if (isLit(stack)) {
            // 关闭提灯
            player.sendMessage(net.minecraft.text.Text.literal("提灯已关闭"), true);
        } else {
            // 开启提灯
            player.sendMessage(net.minecraft.text.Text.literal("提灯已开启"), true);
        }
    }
}
