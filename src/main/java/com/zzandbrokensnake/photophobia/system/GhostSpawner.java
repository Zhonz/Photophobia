package com.zzandbrokensnake.photophobia.system;

import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import com.zzandbrokensnake.photophobia.registry.EntityRegistry;

public class GhostSpawner {

    public static void trySpawnGhost(ServerWorld world, BlockPos pos) {
        // 检查生成条件
        if (canSpawnGhost(world, pos)) {
            spawnGhost(world, pos);
        }
    }

    private static boolean canSpawnGhost(ServerWorld world, BlockPos pos) {
        // 检查光照等级（幽灵在黑暗中生成）
        if (world.getLightLevel(pos) > 7) {
            return false;
        }

        // 检查是否有玩家在附近
        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false) != null) {
            return true;
        }

        return false;
    }

    private static void spawnGhost(ServerWorld world, BlockPos pos) {
        // 寻找合适的生成位置
        BlockPos spawnPos = findSpawnPosition(world, pos);
        if (spawnPos == null) {
            return;
        }

        // 生成幽灵
        var ghost = EntityRegistry.GHOST.create(world);
        if (ghost != null) {
            ghost.refreshPositionAndAngles(spawnPos, world.getRandom().nextFloat() * 360.0F, 0.0F);
            ghost.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.NATURAL, null, null);

            // 检查生成条件
            if (SpawnHelper.isClearForSpawn(world, spawnPos, world.getBlockState(spawnPos),
                    world.getFluidState(spawnPos), ghost.getType())) {
                world.spawnEntity(ghost);

                // 播放生成音效
                world.playSound(null, spawnPos,
                        net.minecraft.sound.SoundEvents.ENTITY_PHANTOM_AMBIENT,
                        net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 0.8f);
            }
        }
    }

    private static BlockPos findSpawnPosition(ServerWorld world, BlockPos center) {
        // 在中心位置周围寻找合适的生成点
        for (int i = 0; i < 10; i++) {
            BlockPos candidate = center.add(
                    world.getRandom().nextInt(16) - 8,
                    world.getRandom().nextInt(8) - 4,
                    world.getRandom().nextInt(16) - 8);

            // 检查位置是否有效
            if (isValidSpawnPosition(world, candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isValidSpawnPosition(ServerWorld world, BlockPos pos) {
        // 检查位置是否在地面上方且有足够的空间
        return world.isAir(pos) &&
                world.isAir(pos.up()) &&
                !world.isAir(pos.down()) &&
                world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }
}
