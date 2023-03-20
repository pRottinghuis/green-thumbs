package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GreenThumbs.ID);

    public static final RegistryObject<BlockEntityType<GTCropBlockEntity>> GT_WHEAT = BLOCK_ENTITIES.register("gt_wheat", () -> BlockEntityType.Builder.of(GTCropBlockEntity::new, GTBlocks.GT_WHEAT.get()).build(null));
}
