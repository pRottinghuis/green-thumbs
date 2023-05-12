package com.cfrishausen.greenthumbs.event;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class EventHandler {

    public static void register() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;

//        forge.addListener(EventHandler::onRightClickBlock);
        forge.addListener(EventHandler::createNewRegistry);
    }

//    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
//        ItemStack stack = event.getItemStack();
//        Item item = stack.getItem();
//        InteractionHand hand = event.getHand();
//        Level level = event.getLevel();
//        Player player = event.getEntity();
//        boolean isSeeds = GTItems.GT_REPLACEMENTS.containsKey(item); // TODO make abstract for all seeds
//        if (isSeeds) {
//            // if minecraft item is clicked, place green thumbs replacement
//            GTGenomeBlockItem gtItem = GTItems.GT_REPLACEMENTS.get(item).get();
//
//            // create seed itemstack with genome
//            ItemStack seedsStack = new ItemStack(gtItem);
//            seedsStack.getOrCreateTag().put(Genome.GENOME_TAG, new Genome(level.random).writeTag());
//
//            stack.useOn(new UseOnContext(level, player, hand, new ItemStack(gtItem), event.getHitVec()));
//            event.setUseItem(Event.Result.DENY);
//            event.setCanceled(true);
//            player.swing(hand);
//        }
//    }

    public static void createNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<ICropSpecies>().setName(new ResourceLocation(GreenThumbs.ID, "crop_type")));
    }
}
