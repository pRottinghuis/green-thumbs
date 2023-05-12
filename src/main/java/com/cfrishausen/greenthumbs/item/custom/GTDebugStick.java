package com.cfrishausen.greenthumbs.item.custom;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.event.ComponentListener;

public class GTDebugStick extends Item {
    public GTDebugStick(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level.isClientSide()) {
            Player player = pContext.getPlayer();
            BlockPos clickedPos = pContext.getClickedPos();
            BlockEntity entity = level.getBlockEntity(clickedPos);
            if (entity instanceof GTCropBlockEntity cropEntity) {
                CompoundTag updateTag = cropEntity.getUpdateTag();
                if (updateTag.contains(NBTTags.INFO_TAG)) {
                    updateTag = updateTag.getCompound(NBTTags.INFO_TAG);
                    player.sendSystemMessage(Component.literal("Crop at " + clickedPos.toShortString() + ":"));
                    player.sendSystemMessage(Component.literal("Age: " + updateTag.getInt(NBTTags.AGE_TAG)));
                    player.sendSystemMessage(Component.literal("Species: " + updateTag.getString(NBTTags.CROP_SPECIES_TAG)));
                    if (updateTag.contains(NBTTags.GENOME_TAG)) {
                        CompoundTag genomeTag = updateTag.getCompound(NBTTags.GENOME_TAG);
                        genomeTag.getAllKeys().forEach(geneKey -> {
                            player.sendSystemMessage(Component.literal(geneKey + ": " + genomeTag.getString(geneKey)));
                        });
                    }
                }
            }
        }
        return super.useOn(pContext);
    }
}
