package com.zzandbrokensnake.photophobia.registry;

import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.items.BlueEggItem;
import com.zzandbrokensnake.photophobia.items.FuelItem;
import com.zzandbrokensnake.photophobia.items.GlowingMushroomItem;
import com.zzandbrokensnake.photophobia.items.LanternItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry {
        // 物品定义
        public static final Item LANTERN = new LanternItem(new Item.Settings().maxCount(1));
        public static final Item CALM_CAPSULE = new Item(new Item.Settings().maxCount(16));
        public static final Item CALM_POWDER = new Item(new Item.Settings().maxCount(32));
        public static final Item CALM_SPRAY = new Item(new Item.Settings().maxCount(8));
        public static final Item BLUE_EGG = new BlueEggItem(new Item.Settings().maxCount(1));
        public static final Item FUEL = new FuelItem(new Item.Settings().maxCount(64));

        // 荧光蘑菇
        public static final Item BLUE_MUSHROOM = new GlowingMushroomItem(
                        new Item.Settings().maxCount(64),
                        GlowingMushroomItem.MushroomType.BLUE);
        public static final Item GREEN_MUSHROOM = new GlowingMushroomItem(
                        new Item.Settings().maxCount(64),
                        GlowingMushroomItem.MushroomType.GREEN);
        public static final Item RED_MUSHROOM = new GlowingMushroomItem(
                        new Item.Settings().maxCount(64),
                        GlowingMushroomItem.MushroomType.RED);
        public static final Item PURPLE_MUSHROOM = new GlowingMushroomItem(
                        new Item.Settings().maxCount(64),
                        GlowingMushroomItem.MushroomType.PURPLE);

        public static void register() {
                // 注册物品
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "lantern"), LANTERN);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "calm_capsule"), CALM_CAPSULE);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "calm_powder"), CALM_POWDER);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "calm_spray"), CALM_SPRAY);

                // 注册荧光蘑菇
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "blue_mushroom"),
                                BLUE_MUSHROOM);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "green_mushroom"),
                                GREEN_MUSHROOM);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "red_mushroom"), RED_MUSHROOM);
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "purple_mushroom"),
                                PURPLE_MUSHROOM);

                // 注册蓝之卵
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "blue_egg"), BLUE_EGG);

                // 注册燃料
                Registry.register(Registries.ITEM, new Identifier(PhotophobiaMod.MOD_ID, "fuel"), FUEL);

                PhotophobiaMod.LOGGER.info("Item registry initialized");
        }
}
