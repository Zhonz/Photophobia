package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.network.HeartRateSyncPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkRegistry {
    public static final Identifier HEART_RATE_SYNC_ID = new Identifier(PhotophobiaMod.MOD_ID, "heart_rate_sync");
    public static final Identifier CONCEALMENT_STATE_ID = new Identifier(PhotophobiaMod.MOD_ID, "concealment_state");

    public static void register() {
        // 注册数据包接收器
        ServerPlayNetworking.registerGlobalReceiver(HEART_RATE_SYNC_ID, HeartRateSyncPacket::receive);

        PhotophobiaMod.LOGGER.info("Network registry initialized");
    }
}
