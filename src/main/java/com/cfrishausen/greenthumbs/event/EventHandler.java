package com.cfrishausen.greenthumbs.event;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.GTWheatBlock;
import com.cfrishausen.greenthumbs.client.model.block.GTWheatBakedModel;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import java.util.ArrayList;
import java.util.Map;

public class EventHandler {

    public static void register() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;

        forge.addListener(EventHandler::onRightClickBlock);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        Player player = event.getEntity();
        boolean isWheat = stack.is(Items.WHEAT_SEEDS);
        if (isWheat) {
            stack.useOn(new UseOnContext(level, player, hand, new ItemStack(GTItems.GT_WHEAT_SEEDS.get()), event.getHitVec()));
        }
    }
}
