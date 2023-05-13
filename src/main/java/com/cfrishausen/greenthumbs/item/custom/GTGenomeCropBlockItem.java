package com.cfrishausen.greenthumbs.item.custom;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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

public class GTGenomeCropBlockItem extends BlockItem {

    public GTGenomeCropBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {

        if (true/*pLevel.isClientSide()*/) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof GTCropBlockEntity cropEntity) {
                CompoundTag tag = pStack.getTag();
                // See if seed has a correct tag
                if (tag != null && tag.contains(NBTTags.INFO_TAG)) {
                    // give tag back to BlockEntity for a load
                    cropEntity.load(tag);
                } else {

                }
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (pStack.hasTag()) {
            CompoundTag tag = pStack.getTag();
            if (tag.contains(NBTTags.INFO_TAG)) {
                CompoundTag genomeTag = tag.getCompound(NBTTags.INFO_TAG).getCompound(NBTTags.GENOME_TAG);
                // Add genes for tooltip based on what is in nbt tag
                for (String genomeTagKey : genomeTag.getAllKeys()) {
                    String geneStr = genomeTag.getString(genomeTagKey);
                    pTooltip.add(Component.literal(genomeTagKey + ": " + geneStr).withStyle(style -> style.withColor(ChatFormatting.GREEN)));
                }
            }
        }
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        Player player  = pContext.getPlayer();
        CompoundTag nbt = player.getItemInHand(InteractionHand.MAIN_HAND).getTag();
        CompoundTag saveTag = nbt.getCompound(NBTTags.INFO_TAG);
        if (saveTag.contains(NBTTags.CROP_SPECIES_TAG)) {
            saveTag.getString(NBTTags.CROP_SPECIES_TAG);
            ICropSpecies cropSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValue(new ResourceLocation(saveTag.getString(NBTTags.CROP_SPECIES_TAG)));
            return cropSpecies.canSurvive(pState, pContext.getLevel(), pContext.getClickedPos(), this.getBlock());
        } else {
            player.sendSystemMessage(Component.literal("Seed has no genome").withStyle(style -> style.withColor(ChatFormatting.RED)));
        }
        return false;
    }
}
