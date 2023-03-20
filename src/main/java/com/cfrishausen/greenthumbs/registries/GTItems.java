package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.item.custom.GTDebugStick;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class GTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, GreenThumbs.ID);

    public static final RegistryObject<Item> GT_DEBUG_STICK = ITEMS.register("gt_debug_stick", () -> new GTDebugStick(new Item.Properties()));

    public static final RegistryObject<GTGenomeBlockItem> GT_CARROT = registerGenomeBlockItem("gt_carrot", GTBlocks.GT_CARROTS);


    public static final Map <Item, RegistryObject<GTGenomeBlockItem>> GT_REPLACEMENTS = new HashMap<>() {{
       put(Items.CARROT, GT_CARROT);
    }};

    // Assume same name as block

    public static RegistryObject<GTGenomeBlockItem> registerGenomeBlockItem(String path, RegistryObject<? extends Block> block) {
        return ITEMS.register(path, () -> new GTGenomeBlockItem(block.get(), new Item.Properties()));
    }

}
