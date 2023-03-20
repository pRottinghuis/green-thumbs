package com.cfrishausen.greenthumbs.item.custom;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GTGenomeBlockItem extends BlockItem {

    public GTGenomeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        // Must be client
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof GTCropBlockEntity cropEntity) {
                CompoundTag tag = pStack.getTag();
                // See if seed has genome loaded to tag
                if (tag != null && tag.contains(GTCropBlockEntity.INFO_TAG)) {
                    // give tag back to BlockEntity for a load
                    cropEntity.load(tag);
                }
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (pStack.hasTag()) {
            CompoundTag tag = pStack.getTag();
            if (tag.contains(GTCropBlockEntity.INFO_TAG)) {
                CompoundTag genomeTag = tag.getCompound(Genome.GENOME_TAG);

                // Add genes for tooltip
                addTooltipFromTag(genomeTag, pTooltip, Genome.GROWTH_SPEED);
            }
        }

        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    private void addTooltipFromTag(CompoundTag tag, List<Component> pTooltip, String geneKey) {
        String geneStr = tag.getString(geneKey);
        pTooltip.add(Component.literal(geneKey + ": " + geneStr).withStyle(style -> style.withColor(ChatFormatting.GREEN)));
    }
}
