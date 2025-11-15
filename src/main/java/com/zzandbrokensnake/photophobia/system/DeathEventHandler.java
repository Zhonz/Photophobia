package com.zzandbrokensnake.photophobia.system;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;

public class DeathEventHandler {

    public static void initialize() {
        // 监听玩家死亡事件
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                // 玩家死亡时，确保提灯不掉落
                preserveLanternOnDeath(oldPlayer, newPlayer);
            }
        });
    }

    /**
     * 在玩家死亡时保留提灯
     */
    private static void preserveLanternOnDeath(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        // 检查旧玩家是否有提灯
        ItemStack oldLantern = PlayerLanternSystem.getPlayerLantern(oldPlayer);
        if (!oldLantern.isEmpty()) {
            // 将提灯转移到新玩家
            transferLanternToNewPlayer(oldLantern, newPlayer);
        }
    }

    /**
     * 将提灯转移到新玩家
     */
    private static void transferLanternToNewPlayer(ItemStack lantern, ServerPlayerEntity newPlayer) {
        // 确保提灯状态被保留
        boolean wasLit = lantern.getOrCreateNbt().getBoolean("Lit");
        int damage = lantern.getDamage();

        // 创建新的提灯并保持状态
        ItemStack newLantern = new ItemStack(ItemRegistry.LANTERN);
        newLantern.getOrCreateNbt().putBoolean("Lit", wasLit);
        newLantern.setDamage(damage);

        // 尝试将提灯放入新玩家背包
        if (!newPlayer.getInventory().insertStack(newLantern)) {
            // 如果背包已满，直接给予玩家
            newPlayer.giveItemStack(newLantern);
        }

        // 发送提示消息
        newPlayer.sendMessage(net.minecraft.text.Text.literal("你的提灯在死亡时被保留了下来"), false);
    }

    /**
     * 检查玩家是否应该保留提灯
     */
    public static boolean shouldPreserveLantern(ServerPlayerEntity player) {
        return PlayerLanternSystem.hasLantern(player);
    }
}
