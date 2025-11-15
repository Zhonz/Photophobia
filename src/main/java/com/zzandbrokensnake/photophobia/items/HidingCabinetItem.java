package com.zzandbrokensnake.photophobia.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.system.HeartRateManager;

public class HidingCabinetItem extends Item {
    private int usesRemaining;

    public HidingCabinetItem(Settings settings) {
        super(settings.maxCount(1));
        this.usesRemaining = 3; // 初始使用次数
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (player == null) {
            return ActionResult.FAIL;
        }

        // 检查是否可以放置柜子（在固体方块上）
        if (!state.isSolidBlock(world, pos)) {
            return ActionResult.FAIL;
        }

        // 检查使用次数
        if (usesRemaining <= 0) {
            player.sendMessage(net.minecraft.text.Text.literal("柜子已损坏"), true);
            return ActionResult.FAIL;
        }

        // 进入藏匿状态
        enterHidingState(player, pos);

        // 消耗使用次数
        usesRemaining--;
        if (usesRemaining <= 0) {
            context.getStack().decrement(1);
            player.sendMessage(net.minecraft.text.Text.literal("柜子已损坏"), true);
        }

        // 播放音效
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);

        return ActionResult.SUCCESS;
    }

    private void enterHidingState(PlayerEntity player, BlockPos cabinetPos) {
        // 立即降低心率
        HeartRateManager.forceHeartRate(player.getUuid(),
                Math.max(60, HeartRateManager.getHeartRate(player.getUuid()) - 30));

        // 应用藏匿效果
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                com.zzandbrokensnake.photophobia.registry.StatusEffectRegistry.CONCEALMENT_EFFECT,
                200, // 10秒
                0,
                false,
                false,
                true));

        // 发送消息
        player.sendMessage(net.minecraft.text.Text.literal("进入藏匿状态，心率降低"), true);
    }

    public int getUsesRemaining() {
        return usesRemaining;
    }

    @Override
    public void appendTooltip(net.minecraft.item.ItemStack stack, net.minecraft.world.World world,
            java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(net.minecraft.text.Text.literal("剩余使用次数: " + usesRemaining));
        tooltip.add(net.minecraft.text.Text.literal("右键固体方块进入藏匿状态"));
        tooltip.add(net.minecraft.text.Text.literal("立即降低30 BPM，获得藏匿效果"));
    }
}
