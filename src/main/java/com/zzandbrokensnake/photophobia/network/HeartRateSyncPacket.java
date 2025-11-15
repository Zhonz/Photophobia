package com.zzandbrokensnake.photophobia.network;

import com.zzandbrokensnake.photophobia.registry.NetworkRegistry;
import com.zzandbrokensnake.photophobia.system.HeartRateManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class HeartRateSyncPacket {
    private final UUID playerUUID;
    private final int heartRate;
    private final boolean isOverloading;

    public HeartRateSyncPacket(UUID playerUUID, int heartRate, boolean isOverloading) {
        this.playerUUID = playerUUID;
        this.heartRate = heartRate;
        this.isOverloading = isOverloading;
    }

    public static void sendToPlayer(ServerPlayerEntity player, int heartRate, boolean isOverloading) {
        HeartRateSyncPacket packet = new HeartRateSyncPacket(player.getUuid(), heartRate, isOverloading);
        PacketByteBuf buf = createPacketBuffer(packet);
        ServerPlayNetworking.send(player, NetworkRegistry.HEART_RATE_SYNC_ID, buf);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
            PacketByteBuf buf, PacketSender responseSender) {
        UUID playerUUID = buf.readUuid();
        int heartRate = buf.readInt();
        boolean isOverloading = buf.readBoolean();

        // 在服务器端处理心率数据
        server.execute(() -> {
            HeartRateManager.forceHeartRate(playerUUID, heartRate);
            HeartRateManager.HeartRateData data = HeartRateManager.getHeartRateData(playerUUID);
            data.setOverloading(isOverloading);
        });
    }

    private static PacketByteBuf createPacketBuffer(HeartRateSyncPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeUuid(packet.playerUUID);
        buf.writeInt(packet.heartRate);
        buf.writeBoolean(packet.isOverloading);
        return buf;
    }
}
