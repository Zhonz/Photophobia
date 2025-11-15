package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {
    public static final ItemGroup PHOTOPHOBIA_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ItemRegistry.CALM_POWDER))
            .displayName(Text.translatable("itemGroup.photophobia.main"))
            .entries((displayContext, entries) -> {
                // 添加所有畏光模组的物品
                entries.add(ItemRegistry.LANTERN);
                entries.add(ItemRegistry.CALM_CAPSULE);
                entries.add(ItemRegistry.CALM_POWDER);
                entries.add(ItemRegistry.CALM_SPRAY);
                entries.add(ItemRegistry.BLUE_EGG);
                entries.add(ItemRegistry.BLUE_MUSHROOM);
                entries.add(ItemRegistry.GREEN_MUSHROOM);
                entries.add(ItemRegistry.RED_MUSHROOM);
                entries.add(ItemRegistry.PURPLE_MUSHROOM);
            })
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(PhotophobiaMod.MOD_ID, "main"),
                PHOTOPHOBIA_GROUP);
        PhotophobiaMod.LOGGER.info("Item group registry initialized");
    }
}
