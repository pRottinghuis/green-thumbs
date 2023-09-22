package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.data.loot.GTLootFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GTLootFunctions {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPE = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE.key(), GreenThumbs.ID);

    public static final RegistryObject<LootItemFunctionType> GT_LOOT_FUNCTION = LOOT_FUNCTION_TYPE.register("gt_loot_function", () -> {
        return new LootItemFunctionType(new GTLootFunction.Serializer());
    });

}
