package com.cfrishausen.greenthumbs.item.custom;

import com.cfrishausen.greenthumbs.block.GTWheatBlock;
import com.cfrishausen.greenthumbs.block.entity.GTWheatBlockEntity;
import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GTDebugStick extends Item {
    public GTDebugStick(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!level.isClientSide()) {
            Player player = pContext.getPlayer();
            BlockPos clickedPos = pContext.getClickedPos();
            BlockEntity entity = level.getBlockEntity(clickedPos);
            if (entity instanceof GTWheatBlockEntity cropEntity) {
                Genome cropGenome = cropEntity.getGenome();
                player.sendSystemMessage(Component.literal("Crop at " + clickedPos.toShortString() + ":"));
                player.sendSystemMessage(Component.literal("Genome: " + cropGenome + ","));
                player.sendSystemMessage(Component.literal("Age: " + cropEntity.getAge()));
            }
        }
        return super.useOn(pContext);
    }
}
