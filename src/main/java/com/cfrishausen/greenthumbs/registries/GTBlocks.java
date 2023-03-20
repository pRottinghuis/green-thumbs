package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class GTBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, GreenThumbs.ID);

    public static final RegistryObject<Block> GT_CARROTS = BLOCKS.register("gt_carrot_block",
            () -> new GTSimpleCropBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP),
                    GTItems.GT_CARROT));

    public static final Map<Block, RegistryObject<Block>> GT_REPLACEMENTS = new HashMap<>() {{
        put(Blocks.CARROTS, GT_CARROTS);
    }};
}
