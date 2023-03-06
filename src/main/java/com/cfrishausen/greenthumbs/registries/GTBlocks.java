package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.GTWheatBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, GreenThumbs.ID);

    public static final RegistryObject<Block> GT_WHEAT = BLOCKS.register("gt_wheat", () -> new GTWheatBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
}
