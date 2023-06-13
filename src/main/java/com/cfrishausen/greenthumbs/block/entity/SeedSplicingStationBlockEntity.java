package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.screen.SeedSplicingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Credit: Tutorials By Kaupenjoe | https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.19/tree/22-blockEntities
public class SeedSplicingStationBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;

    // Needs to be set by a ContainerData so cannot be final
    private int maxProgress = 78;

    public SeedSplicingStationBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.SEED_SPLICING_STATION_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SeedSplicingStationBlockEntity.this.progress;
                    case 1 -> SeedSplicingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SeedSplicingStationBlockEntity.this.progress = value;
                    case 1 -> SeedSplicingStationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return GTBlocks.SEED_SPLICING_STATION.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player pPlayer) {
        return new SeedSplicingStationMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("seed_splicing_station.progress", this.progress);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("seed_splicing_station.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, SeedSplicingStationBlockEntity pEntity) {

        if(level.isClientSide()) {
            return;
        }

            if(hasRecipe(pEntity)) {
            pEntity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(SeedSplicingStationBlockEntity pEntity) {

        if(hasRecipe(pEntity)) {
            ItemStack seed1 = pEntity.itemHandler.getStackInSlot(0);
            ItemStack seed2 = pEntity.itemHandler.getStackInSlot(1);

            CompoundTag splicedTag = Genome.fullSpliceTag(seed1.getTag(), seed2.getTag());

            pEntity.itemHandler.extractItem(0, 1, false);
            pEntity.itemHandler.extractItem(1, 1, false);
            ItemStack outputStack = new ItemStack(seed1.getItem(), 1);
            outputStack.setTag(splicedTag);
            pEntity.itemHandler.setStackInSlot(2, outputStack);
            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(SeedSplicingStationBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        boolean hasGenomeBlockItems = entity.itemHandler.getStackInSlot(0).getItem() instanceof GTGenomeCropBlockItem && entity.itemHandler.getStackInSlot(1).getItem() instanceof GTGenomeCropBlockItem;

        return hasGenomeBlockItems && sameSpecies(inventory) && outputSlotEmpty(inventory);
    }

    private static boolean sameSpecies(SimpleContainer inventory) {
        CompoundTag seed1Tag = inventory.getItem(0).getTag();
        CompoundTag seed2Tag = inventory.getItem(0).getTag();

        if (seed1Tag.contains(NBTTags.INFO_TAG) && seed1Tag.contains(NBTTags.INFO_TAG)) {
            String species1 =  seed1Tag.getCompound(NBTTags.INFO_TAG).getString(NBTTags.CROP_SPECIES_TAG);
            String species2 =  seed2Tag.getCompound(NBTTags.INFO_TAG).getString(NBTTags.CROP_SPECIES_TAG);
            return species1.equals(species2);
        }
        return false;
    }

    private static boolean outputSlotEmpty(SimpleContainer inventory) {
        return inventory.getItem(3).isEmpty();
    }

}