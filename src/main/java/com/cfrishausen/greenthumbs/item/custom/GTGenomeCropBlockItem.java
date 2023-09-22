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
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
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
                    tooltips.add(Component.translatable(genomeTagKey).append(Component.literal(": " + geneStr)).withStyle(ChatFormatting.DARK_GREEN));
                }
            } else {
                GreenThumbs.LOGGER.warn("Can't add genome tooltip because {} compound is empty", NBTTags.GENOME_TAG);
            }
        }
        return tooltips;
    }



    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        if (!context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext)) {
            return false;
        }

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).hasTag()) {
            GreenThumbs.LOGGER.warn("{} has null nbt", this);
            return false;
        }

        CompoundTag nbt = player.getItemInHand(InteractionHand.MAIN_HAND).getTag();
        CompoundTag saveTag = nbt.getCompound(NBTTags.INFO_TAG);
        if (saveTag.contains(NBTTags.CROP_SPECIES_TAG) && saveTag.contains(NBTTags.GENOME_TAG)) {
            saveTag.getString(NBTTags.CROP_SPECIES_TAG);
            ICropSpecies cropSpecies = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValue(new ResourceLocation(saveTag.getString(NBTTags.CROP_SPECIES_TAG)));
            Genome genome = new Genome(saveTag.getCompound(NBTTags.GENOME_TAG));
            // Can survive brightness is 1 lower than light required to grow.
            int brightnessReq = genome.getLightTolerance() == 1 ? 0 : genome.getLightTolerance() - 1;
            Level level = context.getLevel();
            BlockPos clickedPos = context.getClickedPos();
            return cropSpecies.mayPlaceOn(context.getLevel().getBlockState(context.getClickedPos().below())) && (level.getRawBrightness(clickedPos, 0) >= brightnessReq || level.canSeeSky(clickedPos));
        } else {
            GreenThumbs.LOGGER.warn("Missing crop species and/or genome when trying to place GTCropBlockItem");
        }
        return false;
    }
}
