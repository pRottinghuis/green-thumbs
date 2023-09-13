package com.cfrishausen.greenthumbs.data.loot;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.registries.GTLoot;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class GTLootFunction extends LootItemConditionalFunction {

    public GTLootFunction(LootItemCondition[] pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack pStack, LootContext context) {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity != null) {
            if (blockEntity instanceof GTCropBlockEntity cropBlockEntity) {
                ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
                if (tool != null) {
                    cropBlockEntity.getCropSpecies().drops(cropBlockEntity, context.getLevel(), cropBlockEntity.getBlockPos(), tool.getAllEnchantments(), false);
                }
            }
        } else {
            GreenThumbs.LOGGER.warn("{} is missing block entity context", this);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public LootItemFunctionType getType() {
        return GTLoot.GT_LOOT_FUNCTION.get();
    }

    public static LootItemConditionalFunction.Builder<?> gtLoot() {
        return simpleBuilder(GTLootFunction::new);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<GTLootFunction> {

        @Override
        public GTLootFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new GTLootFunction(pConditions);
        }
    }
}
