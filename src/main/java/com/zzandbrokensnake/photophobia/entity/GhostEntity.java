package com.zzandbrokensnake.photophobia.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.system.HeartRateManager;
import com.zzandbrokensnake.photophobia.entity.ai.PossessPlayerGoal;

public class GhostEntity extends HostileEntity {
    private PlayerEntity possessedPlayer = null;
    private long possessionStartTime = 0;
    private int possessionDuration = 0;

    public GhostEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createGhostAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PossessPlayerGoal(this, 1.0));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // 更新附身状态
        updatePossession();

        // 幽灵特殊效果
        if (this.age % 20 == 0) {
            applyGhostEffects();
        }
    }

    private void updatePossession() {
        if (possessedPlayer != null) {
            // 检查附身是否结束
            if (this.getWorld().getTime() - possessionStartTime > possessionDuration) {
                endPossession();
            } else {
                // 持续附身效果
                applyPossessionEffects();
            }
        }
    }

    private void applyGhostEffects() {
        // 幽灵存在时增加附近玩家的心率
        if (!this.getWorld().isClient()) {
            this.getWorld().getPlayers().stream()
                    .filter(player -> player.squaredDistanceTo(this) < 256) // 16格范围内
                    .forEach(player -> {
                        HeartRateManager.increaseThreat(player, 5);
                    });
        }
    }

    private void applyPossessionEffects() {
        if (possessedPlayer != null && !this.getWorld().isClient()) {
            // 附身期间持续增加心率
            HeartRateManager.increaseThreat(possessedPlayer, 10);

            // 视觉扭曲效果
            possessedPlayer.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.NAUSEA, 100, 0, false, false, true));

            // 随机移动控制
            if (this.getRandom().nextFloat() < 0.1f) {
                possessedPlayer.setVelocity(
                        (this.getRandom().nextFloat() - 0.5f) * 0.5f,
                        possessedPlayer.getVelocity().y,
                        (this.getRandom().nextFloat() - 0.5f) * 0.5f);
            }
        }
    }

    public boolean startPossession(PlayerEntity player, int duration) {
        if (possessedPlayer != null)
            return false;

        this.possessedPlayer = player;
        this.possessionStartTime = this.getWorld().getTime();
        this.possessionDuration = duration;

        // 初始附身效果
        HeartRateManager.increaseThreat(player, 30);
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.BLINDNESS, 60, 0, false, false, true));

        // 播放音效
        this.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PHANTOM_AMBIENT,
                net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 0.8f);

        return true;
    }

    public void endPossession() {
        if (possessedPlayer != null) {
            // 附身结束效果
            if (!this.getWorld().isClient()) {
                possessedPlayer.sendMessage(net.minecraft.text.Text.literal("幽灵离开了你的身体"), true);
            }

            // 播放音效
            this.getWorld().playSound(null, possessedPlayer.getBlockPos(), SoundEvents.ENTITY_PHANTOM_DEATH,
                    net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 1.2f);

            this.possessedPlayer = null;
            this.possessionStartTime = 0;
            this.possessionDuration = 0;
        }
    }

    public boolean isPossessing() {
        return possessedPlayer != null;
    }

    public PlayerEntity getPossessedPlayer() {
        return possessedPlayer;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (possessedPlayer != null) {
            nbt.putUuid("PossessedPlayer", possessedPlayer.getUuid());
            nbt.putLong("PossessionStartTime", possessionStartTime);
            nbt.putInt("PossessionDuration", possessionDuration);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("PossessedPlayer")) {
            // 在服务器端恢复附身状态
            if (!this.getWorld().isClient()) {
                PlayerEntity player = this.getWorld().getPlayerByUuid(nbt.getUuid("PossessedPlayer"));
                if (player != null) {
                    this.possessedPlayer = player;
                    this.possessionStartTime = nbt.getLong("PossessionStartTime");
                    this.possessionDuration = nbt.getInt("PossessionDuration");
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }
}
