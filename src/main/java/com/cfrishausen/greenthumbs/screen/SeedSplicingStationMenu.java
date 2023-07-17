package com.cfrishausen.greenthumbs.screen;

import com.cfrishausen.greenthumbs.block.entity.SeedSplicingStationBlockEntity;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTMenuTypes;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;


// Credit: Tutorials By Kaupenjoe | https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.19/tree/22-blockEntities
public class SeedSplicingStationMenu extends AbstractContainerMenu {

    public final SeedSplicingStationBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private SlotItemHandler seedSlot1;
    private SlotItemHandler seedSlot2;

    private SlotItemHandler outputSlot;

    public SeedSplicingStationMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public SeedSplicingStationMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(GTMenuTypes.SEED_SPLICING_STATION_MENU.get(), id);
        checkContainerSize(inv, 3);
        blockEntity = (SeedSplicingStationBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);



        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            // input seed  1 and 2
            this.addSlot(this.seedSlot1 = new SlotItemHandler(handler, 0, 24, 22));
            this.addSlot(this.seedSlot2 = new SlotItemHandler(handler, 1, 24, 58));
            // output seed
            this.addSlot(this.outputSlot = new SlotItemHandler(handler, 2, 134, 40));
        });

        addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int pId) {
        if (hasRecipe()) {
            // indicate non creative-like GameType and then check for high enough level

            // Creative doesn't deal with xp
            if (player.getAbilities().instabuild) {
                data.set(2, 1);
                player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.0F);
                return true;
            } else {
                if (player.experienceLevel >= 3) {
                    data.set(2, 1);
                    player.experienceLevel -= 3;
                    player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.0F);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 60; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, GTBlocks.SEED_SPLICING_STATION.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 98 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 156));
        }
    }

    public boolean hasRecipe() {
        SimpleContainer inventory = new SimpleContainer(3);
        inventory.setItem(0, seedSlot1.getItem());
        inventory.setItem(1, seedSlot2.getItem());
        inventory.setItem(2, outputSlot.getItem());
        return SeedSplicingStationBlockEntity.hasRecipe(inventory);
    }


    public List<Component> getToolTipList() {
        List<Component> componentList = Lists.newArrayList();
        ItemStack seed1 = seedSlot1.getItem();
        ItemStack seed2 = seedSlot2.getItem();
        // Add name of item
        componentList.add(Component.translatable(seed1.getDescriptionId()).withStyle(ChatFormatting.WHITE));
        // Get what the tag of the spliced item will be from genome
        CompoundTag probableSpliceTag = Genome.fullSpliceTag(seed1.getTag(), seed2.getTag());
        // GTGenomeCropBlockItem already has functionality for getting the tool tip we need for the possible resultant seed.
        componentList.addAll(GTGenomeCropBlockItem.getToolTips(probableSpliceTag));
        return componentList;
    }
}
