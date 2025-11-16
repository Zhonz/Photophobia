package com.zzandbrokensnake.photophobia.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.zzandbrokensnake.photophobia.system.HeartRateManager;
import com.zzandbrokensnake.photophobia.system.WorldEventHandler;
import com.zzandbrokensnake.photophobia.registry.PhotophobiaConfig;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;

import java.util.Collection;

/**
 * 畏光模组指令系统
 * 提供心率查询、系统控制等实用功能
 */
public class PhotophobiaCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerPhotophobiaCommands(dispatcher);
        });
    }

    private static void registerPhotophobiaCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("photophobia")
                .requires(source -> source.hasPermissionLevel(2)) // 需要权限等级2
                .then(CommandManager.literal("heartrate")
                    .executes(PhotophobiaCommands::queryOwnHeartRate)
                    .then(CommandManager.argument("target", EntityArgumentType.players())
                        .executes(PhotophobiaCommands::queryTargetHeartRate)))
                .then(CommandManager.literal("setheartrate")
                    .then(CommandManager.argument("target", EntityArgumentType.players())
                        .then(CommandManager.argument("rate", IntegerArgumentType.integer(60, 200))
                            .executes(PhotophobiaCommands::setHeartRate))))
                .then(CommandManager.literal("resetheartrate")
                    .executes(PhotophobiaCommands::resetOwnHeartRate)
                    .then(CommandManager.argument("target", EntityArgumentType.players())
                        .executes(PhotophobiaCommands::resetTargetHeartRate)))
                .then(CommandManager.literal("toggletimelock")
                    .executes(PhotophobiaCommands::toggleTimeLock))
                .then(CommandManager.literal("status")
                    .executes(PhotophobiaCommands::getSystemStatus))
                .then(CommandManager.literal("giveitems")
                    .executes(PhotophobiaCommands::giveAllItems)
                    .then(CommandManager.argument("target", EntityArgumentType.players())
                        .executes(PhotophobiaCommands::giveAllItemsToTarget)))
                .then(CommandManager.literal("help")
                    .executes(PhotophobiaCommands::showHelp)));
    }

    /**
     * 查询自己的心率
     */
    private static int queryOwnHeartRate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        int heartRate = HeartRateManager.getHeartRate(player.getUuid());
        String status = getHeartRateStatus(heartRate);
        
        source.sendMessage(Text.literal("§6[畏光] §f你的当前心率: §c" + heartRate + " BPM §7(" + status + ")"));
        return 1;
    }

    /**
     * 查询目标玩家的心率
     */
    private static int queryTargetHeartRate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
        
        for (ServerPlayerEntity target : targets) {
            int heartRate = HeartRateManager.getHeartRate(target.getUuid());
            String status = getHeartRateStatus(heartRate);
            
            source.sendMessage(Text.literal("§6[畏光] §f玩家 §e" + target.getName().getString() + " §f的心率: §c" + heartRate + " BPM §7(" + status + ")"));
        }
        
        return targets.size();
    }

    /**
     * 设置目标玩家的心率
     */
    private static int setHeartRate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
        int rate = IntegerArgumentType.getInteger(context, "rate");
        
        for (ServerPlayerEntity target : targets) {
            HeartRateManager.forceHeartRate(target.getUuid(), rate);
            source.sendMessage(Text.literal("§6[畏光] §f已将玩家 §e" + target.getName().getString() + " §f的心率设置为: §c" + rate + " BPM"));
        }
        
        return targets.size();
    }

    /**
     * 重置自己的心率
     */
    private static int resetOwnHeartRate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        HeartRateManager.resetHeartRate(player);
        source.sendMessage(Text.literal("§6[畏光] §f你的心率已重置到基础值"));
        return 1;
    }

    /**
     * 重置目标玩家的心率
     */
    private static int resetTargetHeartRate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
        
        for (ServerPlayerEntity target : targets) {
            HeartRateManager.resetHeartRate(target);
            source.sendMessage(Text.literal("§6[畏光] §f已将玩家 §e" + target.getName().getString() + " §f的心率重置到基础值"));
        }
        
        return targets.size();
    }

    /**
     * 切换时间锁定状态
     */
    private static int toggleTimeLock(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        WorldEventHandler.toggleWorldTimeLock();
        boolean isLocked = WorldEventHandler.isWorldTimeLocked();
        
        source.sendMessage(Text.literal("§6[畏光] §f时间锁定状态: " + (isLocked ? "§a已锁定" : "§c已解锁")));
        return 1;
    }

    /**
     * 获取系统状态
     */
    private static int getSystemStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PhotophobiaConfig config = PhotophobiaConfig.getConfig();
        
        source.sendMessage(Text.literal("§6=== 畏光模组系统状态 ==="));
        source.sendMessage(Text.literal("§f时间锁定: " + (WorldEventHandler.isWorldTimeLocked() ? "§a启用" : "§c禁用")));
        source.sendMessage(Text.literal("§f主线剧情: " + (config.general.enableMainStory ? "§a启用" : "§c禁用")));
        source.sendMessage(Text.literal("§f心率系统倍率: §e" + config.heartRate.heartRateMultiplier));
        source.sendMessage(Text.literal("§f低血量黑屏: " + (config.effects.enableCriticalVisionEffect ? "§a启用" : "§c禁用")));
        source.sendMessage(Text.literal("§f黑屏触发阈值: §e" + config.effects.criticalVisionThreshold + " 血量"));
        
        return 1;
    }

    /**
     * 给予所有畏光物品给自己
     */
    private static int giveAllItems(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        givePhotophobiaItems(player);
        source.sendMessage(Text.literal("§6[畏光] §f已给予所有畏光物品"));
        return 1;
    }

    /**
     * 给予所有畏光物品给目标玩家
     */
    private static int giveAllItemsToTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
        
        for (ServerPlayerEntity target : targets) {
            givePhotophobiaItems(target);
            source.sendMessage(Text.literal("§6[畏光] §f已给予所有畏光物品给玩家 §e" + target.getName().getString()));
        }
        
        return targets.size();
    }

    /**
     * 给予所有畏光物品
     */
    private static void givePhotophobiaItems(ServerPlayerEntity player) {
        // 给予提灯
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.LANTERN));
        
        // 给予燃料
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.FUEL, 16));
        
        // 给予凝神物品
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.CALM_CAPSULE, 8));
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.CALM_POWDER, 8));
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.CALM_SPRAY));
        
        // 给予发光蘑菇
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.BLUE_MUSHROOM, 16));
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.RED_MUSHROOM, 16));
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.GREEN_MUSHROOM, 16));
        player.giveItemStack(new net.minecraft.item.ItemStack(ItemRegistry.PURPLE_MUSHROOM, 16));
    }

    /**
     * 显示帮助信息
     */
    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendMessage(Text.literal("§6=== 畏光模组指令帮助 ==="));
        source.sendMessage(Text.literal("§f/photophobia heartrate §7- 查询自己的心率"));
        source.sendMessage(Text.literal("§f/photophobia heartrate <玩家> §7- 查询指定玩家的心率"));
        source.sendMessage(Text.literal("§f/photophobia setheartrate <玩家> <心率> §7- 设置玩家的心率 (60-200)"));
        source.sendMessage(Text.literal("§f/photophobia resetheartrate §7- 重置自己的心率"));
        source.sendMessage(Text.literal("§f/photophobia resetheartrate <玩家> §7- 重置指定玩家的心率"));
        source.sendMessage(Text.literal("§f/photophobia toggletimelock §7- 切换时间锁定状态"));
        source.sendMessage(Text.literal("§f/photophobia status §7- 显示系统状态"));
        source.sendMessage(Text.literal("§f/photophobia giveitems §7- 给予所有畏光物品"));
        source.sendMessage(Text.literal("§f/photophobia giveitems <玩家> §7- 给予指定玩家所有畏光物品"));
        source.sendMessage(Text.literal("§f/photophobia help §7- 显示此帮助信息"));
        
        return 1;
    }

    /**
     * 获取心率状态描述
     */
    private static String getHeartRateStatus(int heartRate) {
        if (heartRate < 80) {
            return "正常";
        } else if (heartRate < 120) {
            return "警戒";
        } else if (heartRate < 160) {
            return "危险";
        } else {
            return "致命";
        }
    }
}
