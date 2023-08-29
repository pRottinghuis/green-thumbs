package com.cfrishausen.greenthumbs.item.custom;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class GTGenomeCropBlockItem extends ItemNameBlockItem {

    public GTGenomeCropBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            CompoundTag tag = pStack.getTag();
            // See if seed has a correct tag
            if (tag != null && tag.contains(NBTTags.INFO_TAG)) {
                // give tag back to BlockEntity for a load
                cropEntity.load(tag);
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        if (level != null && level.isClientSide()) {
            if (Screen.hasShiftDown()) {
                if (stack.hasTag()) {
                    tooltips.addAll(getToolTips(stack.getTag()));
                }
            } else {
                tooltips.add(Component.literal("<Hold Shift>").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
        super.appendHoverText(stack, level, tooltips, flag);
    }

    public static List<Component> getToolTips(CompoundTag tag) {
        List<Component> tooltips = Lists.newArrayList();
        if (tag.contains(NBTTags.INFO_TAG)) {
            CompoundTag infoTag = tag.getCompound(NBTTags.INFO_TAG);
            CompoundTag genomeTag = infoTag.getCompound(NBTTags.GENOME_TAG);
            if (!genomeTag.isEmpty()) {
                // Add genes for tooltip based on what is in nbt tag
                for (String genomeTagKey : genomeTag.getAllKeys()) {
                    String geneStr = genomeTag.getString(genomeTagKey);
                    tooltips.add(Component.translatable(genomeTagKey).append(Component.literal(": " + geneStr)).withStyle(ChatFormatting.GREEN));
                }
            } else {
                GreenThumbs.LOGGER.warn("Can't add genome tooltip because {} compound is empty", NBTTags.GENOME_TAG);
            }
            tooltips.add(Component.literal(infoTag.getCompound(NBTTags.CROP_STATE_TAG).toString()));
        }
        return tooltips;
    }



    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player  = context.getPlayer();
        CompoundTag nbt = player.getItemInHand(InteractionHand.MAIN_HAND).getTag();
        CompoundTag saveTag = nbt.getCompound(NBTTags.INFO_TAG);
        if (saveTag.contains(NBTTags.CROP_SPECIES_TAG)) {
            saveTag.getString(NBTTags.CROP_SPECIES_TAG);
            ICropSpecies cropSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValue(new ResourceLocation(saveTag.getString(NBTTags.CROP_SPECIES_TAG)));
            return cropSpecies.mayPlaceOn(context.getLevel().getBlockState(context.getClickedPos().below()));
        } else {
            player.sendSystemMessage(Component.literal("Seed has no species").withStyle(style -> style.withColor(ChatFormatting.RED)));
        }
        return false;
    }
}
