package com.zzandbrokensnake.photophobia.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.zzandbrokensnake.photophobia.enchantments.NightWatcherEnchantment;
import com.zzandbrokensnake.photophobia.registry.EnchantmentRegistry;
import com.zzandbrokensnake.photophobia.registry.ItemRegistry;
import com.zzandbrokensnake.photophobia.system.LanternLightSystem;

public class LanternItem extends Item {
    private static final String FUEL_KEY = "Fuel";
    private static final int MAX_DURABILITY = 6000;
    private static final long LAST_FUEL_CHECK_KEY = 0L;

    public LanternItem(Settings settings) {
        super(settings.maxDamage(MAX_DURABILITY));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            // 检查提灯是否点亮
            if (isLit(stack)) {
                // 检查提灯是否在主手或副手
                boolean inMainHand = player.getMainHandStack() == stack;
                boolean inOffHand = player.getOffHandStack() == stack;

                if (inMainHand || inOffHand) {
                    // 提灯在手持位置，正常工作
                    if (!world.isClient) {
                        // 服务器端：消耗耐久和燃料
                        // 每秒消耗1耐久
                        if (world.getTime() % 20 == 0) {
                            consumeDurability(stack, player);
                        }

                        // 每5秒检查一次燃料
                        if (world.getTime() % 100 == 0) {
                            checkAndRefuel(stack, player);
                        }

                        // 增加威胁值
                        increaseThreatLevel(player, 20);

                        // 吸引趋光性怪物
                        attractLightSeekingMobs(player);
                    }

                    // 客户端：提供光照效果
                    if (world.isClient) {
                        LanternLightSystem.provideLightForLantern(player, stack);
                    }
                } else {
                    // 提灯不在手持位置，自动熄灭
                    if (!world.isClient) {
                        stack.getOrCreateNbt().putBoolean("Lit", false);
                        stack.getOrCreateNbt().putInt("CustomModelData", 0);
                        player.sendMessage(net.minecraft.text.Text.literal("提灯已自动熄灭"), true);
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            toggleLantern(stack, user);
        }

        return TypedActionResult.success(stack);
    }

    /**
     * 提灯死亡不掉落
     */
    @Override
    public boolean hasGlint(ItemStack stack) {
        // 提灯始终有附魔光效，表示特殊物品
        return true;
    }

    /**
     * 提灯不能被丢弃
     */
    @Override
    public boolean canBeNested() {
        return false;
    }

    /**
     * 提灯可以作为武器使用，攻击伤害为5
     */
    @Override
    public boolean isSuitableFor(net.minecraft.block.BlockState state) {
        return false; // 提灯不适合挖掘方块
    }

    /**
     * 提灯的攻击速度
     */
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, net.minecraft.block.BlockState state) {
        return 1.0f; // 正常攻击速度
    }

    /**
     * 提灯的攻击伤害为5
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 对目标造成5点伤害
        if (attacker instanceof PlayerEntity player) {
            target.damage(player.getDamageSources().playerAttack(player), 5.0f);
        } else {
            target.damage(attacker.getDamageSources().mobAttack(attacker), 5.0f);
        }
        return true;
    }

    /**
     * 切换提灯状态时设置自定义模型数据
     */
    private void toggleLantern(ItemStack stack, PlayerEntity player) {
        boolean isLit = isLit(stack);
        stack.getOrCreateNbt().putBoolean("Lit", !isLit);

        // 设置自定义模型数据
        if (!isLit) {
            stack.getOrCreateNbt().putInt("CustomModelData", 1); // 点亮状态
            player.sendMessage(net.minecraft.text.Text.literal("提灯已点亮"), true);
        } else {
            stack.getOrCreateNbt().putInt("CustomModelData", 0); // 熄灭状态
            player.sendMessage(net.minecraft.text.Text.literal("提灯已熄灭"), true);
        }
    }

    private boolean isLit(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("Lit");
    }

    private void consumeFuel(ItemStack stack, PlayerEntity player) {
        int currentFuel = stack.getOrCreateNbt().getInt(FUEL_KEY);
        if (currentFuel > 0) {
            stack.getOrCreateNbt().putInt(FUEL_KEY, currentFuel - 1);
        } else {
            // 燃料耗尽，熄灭提灯
            stack.getOrCreateNbt().putBoolean("Lit", false);
            player.sendMessage(net.minecraft.text.Text.literal("提灯燃料耗尽"), true);
        }
    }

    private boolean isInOffhand(PlayerEntity player, ItemStack stack) {
        return player.getOffHandStack() == stack;
    }

    private void increaseThreatLevel(PlayerEntity player, int amount) {
        // 这里会调用心率管理器来增加威胁值
        // HeartRateManager.increaseThreat(player, amount);
    }

    private void consumeDurability(ItemStack stack, PlayerEntity player) {
        // 检查巡夜者附魔等级
        int nightWatcherLevel = 0;
        if (stack.hasEnchantments()) {
            // 遍历附魔列表查找巡夜者附魔
            var enchantments = stack.getEnchantments();
            for (int i = 0; i < enchantments.size(); i++) {
                var enchantment = enchantments.getCompound(i);
                if (enchantment.getString("id").equals("photophobia:night_watcher")) {
                    nightWatcherLevel = enchantment.getInt("lvl");
                    break;
                }
            }
        }
        float durabilityMultiplier = NightWatcherEnchantment.getDurabilityMultiplier(nightWatcherLevel);

        // 根据附魔等级计算耐久消耗
        int durabilityLoss = Math.max(1, (int) (1 * durabilityMultiplier));

        // 每秒消耗耐久，但不损毁
        if (stack.getDamage() < MAX_DURABILITY - durabilityLoss) {
            stack.setDamage(stack.getDamage() + durabilityLoss);
        }
    }

    private void checkAndRefuel(ItemStack stack, PlayerEntity player) {
        // 检查背包中是否有燃料
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem() == ItemRegistry.FUEL && itemStack.getCount() > 0) {
                // 消耗一个燃料，回满耐久
                itemStack.decrement(1);
                stack.setDamage(0);
                player.sendMessage(net.minecraft.text.Text.literal("提灯已自动补充燃料"), true);
                return;
            }
        }
    }

    private void attractLightSeekingMobs(PlayerEntity player) {
        // 这里会吸引趋光性怪物
        // MonsterAttractionSystem.attractLightSeekingMobs(player);
    }
}
