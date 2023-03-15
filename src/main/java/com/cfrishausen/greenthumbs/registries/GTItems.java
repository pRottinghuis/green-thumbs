package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.item.custom.GTDebugStick;
import com.cfrishausen.greenthumbs.item.custom.GTWheatSeeds;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, GreenThumbs.ID);

    public static final RegistryObject<Item> GT_WHEAT_SEEDS = registerCropSeeds("gt_wheat_seeds", GTBlocks.GT_WHEAT);

    public static final RegistryObject<Item> GT_DEBUG_STICK = ITEMS.register("gt_debug_stick", () -> new GTDebugStick(new Item.Properties()));


    // Assume same name as block

    public static RegistryObject<Item> registerCropSeeds(String path, RegistryObject<? extends Block> block) {
        return ITEMS.register(path, () -> new GTWheatSeeds(block.get(), new Item.Properties()));
    }

}
