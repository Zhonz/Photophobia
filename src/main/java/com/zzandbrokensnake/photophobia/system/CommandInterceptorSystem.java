package com.zzandbrokensnake.photophobia.system;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.zzandbrokensnake.photophobia.registry.PhotophobiaConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.Collection;

/**
 * 指令拦截系统
 * 防止玩家通过指令修改时间，保护永恒黑夜模式
 */
public class CommandInterceptorSystem {

    public static void initialize() {
        // 监听指令注册事件
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // 在所有环境（客户端和服务器）都拦截指令
            interceptTimeCommand(dispatcher);
        });
    }

    /**
     * 拦截time指令
     */
    private static void interceptTimeCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        // 获取time指令节点
        CommandNode<ServerCommandSource> timeNode = dispatcher.getRoot().getChild("time");

        if (timeNode != null) {
            // 创建拦截器
            LiteralCommandNode<ServerCommandSource> interceptor = dispatcher.register(
                    com.mojang.brigadier.builder.LiteralArgumentBuilder.<ServerCommandSource>literal("time")
                            .requires(source -> {
                                // 检查配置是否启用time指令拦截
                                PhotophobiaConfig config = PhotophobiaConfig.getConfig();
                                if (config.commands.disableTimeCommand) {
                                    // 所有权限等级都被拦截
                                    return false;
                                }
                                return true;
                            })
                            .executes(context -> {
                                // 执行拦截逻辑
                                return handleTimeCommand(context);
                            })
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                    .<ServerCommandSource>literal("set")
                                    .executes(context -> handleTimeCommand(context))
                                    .then(com.mojang.brigadier.builder.RequiredArgumentBuilder
                                            .<ServerCommandSource, String>argument("time",
                                                    com.mojang.brigadier.arguments.StringArgumentType.word())
                                            .executes(context -> handleTimeCommand(context))))
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                    .<ServerCommandSource>literal("add")
                                    .executes(context -> handleTimeCommand(context))
                                    .then(com.mojang.brigadier.builder.RequiredArgumentBuilder
                                            .<ServerCommandSource, String>argument("time",
                                                    com.mojang.brigadier.arguments.StringArgumentType.word())
                                            .executes(context -> handleTimeCommand(context))))
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                    .<ServerCommandSource>literal("query")
                                    .executes(context -> {
                                        // 允许查询时间
                                        return timeNode.getCommand().run(context);
                                    })
                                    .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                            .<ServerCommandSource>literal("day")
                                            .executes(context -> timeNode.getChild("query").getChild("day").getCommand()
                                                    .run(context)))
                                    .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                            .<ServerCommandSource>literal("daytime")
                                            .executes(context -> timeNode.getChild("query").getChild("daytime")
                                                    .getCommand().run(context)))
                                    .then(com.mojang.brigadier.builder.LiteralArgumentBuilder
                                            .<ServerCommandSource>literal("gametime")
                                            .executes(context -> timeNode.getChild("query").getChild("gametime")
                                                    .getCommand().run(context)))));

            // 重定向所有time子命令到拦截器
            redirectTimeSubcommands(dispatcher, timeNode, interceptor);
        }
    }

    /**
     * 重定向time子命令到拦截器
     */
    private static void redirectTimeSubcommands(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandNode<ServerCommandSource> originalNode,
            CommandNode<ServerCommandSource> interceptor) {
        // 获取所有子命令
        Collection<CommandNode<ServerCommandSource>> children = originalNode.getChildren();

        for (CommandNode<ServerCommandSource> child : children) {
            // 跳过已经处理的子命令
            if (interceptor.getChild(child.getName()) == null) {
                // 将子命令添加到拦截器
                interceptor.addChild(child);
            }
        }

        // 用拦截器替换原始time节点
        dispatcher.getRoot().addChild(interceptor);
    }

    /**
     * 处理time指令拦截
     */
    private static int handleTimeCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PhotophobiaConfig config = PhotophobiaConfig.getConfig();

        if (config.commands.disableTimeCommand) {
            // 发送拦截消息
            ServerCommandSource source = context.getSource();
            source.sendError(Text.literal(config.commands.timeCommandMessage));
            return 0; // 返回0表示命令失败
        } else {
            // 如果禁用拦截，允许执行
            // 这里我们无法直接调用原始命令，所以返回成功但不执行任何操作
            return 1;
        }
    }

    /**
     * 检查是否允许使用time指令
     */
    public static boolean isTimeCommandAllowed(ServerCommandSource source) {
        PhotophobiaConfig config = PhotophobiaConfig.getConfig();

        if (config.commands.disableTimeCommand) {
            // 所有权限等级都被拦截
            return false;
        }

        return true;
    }
}
