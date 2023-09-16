package com.cfrishausen.greenthumbs.block.entity;

import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.data.recipe.SeedSplicingStationRecipe;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTRecipeTypes;
import com.cfrishausen.greenthumbs.screen.SeedSplicingStationMenu;
import com.cfrishausen.greenthumbs.screen.SeedSplicingStationScreen;
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
import net.minecraft.world.item.Item;
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

import java.util.Optional;

// Credit: Tutorials By Kaupenjoe | https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.19/tree/22-blockEntities
public class SeedSplicingStationBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0 || slot == 1) {
                if (stack.getItem().asItem() instanceof GTGenomeCropBlockItem) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isCrafting()) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;

    // Needs to be set by a ContainerData so cannot be final
    private int maxProgress = 78;

    private int buttonClicked = 0;

    int hasRecipe = 0;

    public SeedSplicingStationBlockEntity(BlockPos pos, BlockState state) {
        super(GTBlockEntities.SEED_SPLICING_STATION_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SeedSplicingStationBlockEntity.this.progress;
                    case 1 -> SeedSplicingStationBlockEntity.this.maxProgress;
                    case 2 -> SeedSplicingStationBlockEntity.this.buttonClicked;
                    case 3 -> SeedSplicingStationBlockEntity.this.hasRecipe;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SeedSplicingStationBlockEntity.this.progress = value;
                    case 1 -> SeedSplicingStationBlockEntity.this.maxProgress = value;
                    case 2 -> SeedSplicingStationBlockEntity.this.buttonClicked = value;
                    case 3 -> SeedSplicingStationBlockEntity.this.hasRecipe = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
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
        nbt.putInt("seed_splicing_station.buttonClicked", this.buttonClicked);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("seed_splicing_station.progress");
        buttonClicked = nbt.getInt("seed_splicing_station.buttonClicked");
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

        if(hasRecipe(pEntity) && pEntity.buttonClicked != 0) {
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
        this.buttonClicked = 0;
    }

    private static void craftItem(SeedSplicingStationBlockEntity pEntity) {

        if(hasRecipe(pEntity)) {
            Level level = pEntity.getLevel();
            SimpleContainer inventory = getInventoryCopy(pEntity);
            Optional<SeedSplicingStationRecipe> recipe = level.getRecipeManager().getRecipeFor(SeedSplicingStationRecipe.Type.INSTANCE, inventory, level);


            ItemStack seed1 = pEntity.itemHandler.getStackInSlot(0);
            ItemStack seed2 = pEntity.itemHandler.getStackInSlot(1);

            CompoundTag splicedTag = Genome.fullSpliceTag(seed1.getTag(), seed2.getTag());

            ItemStack outputStack = recipe.get().getResultItem(level.registryAccess());
            outputStack.setTag(splicedTag);
            pEntity.itemHandler.setStackInSlot(2, outputStack);
            pEntity.resetProgress();

            // seeds need to extract after progress is reset because itemstackhandler prevents extract while data is indicating crafting
            pEntity.itemHandler.extractItem(0, 1, false);
            pEntity.itemHandler.extractItem(1, 1, false);

            pEntity.setHasRecipe(0);
        }
    }

    public static boolean hasRecipe(SeedSplicingStationBlockEntity entity) {
        Level level = entity.getLevel();
        SimpleContainer inventory = getInventoryCopy(entity);

        Optional<SeedSplicingStationRecipe> recipe = level.getRecipeManager().getRecipeFor(GTRecipeTypes.SEED_SPLICING_STATION_TYPE.get(), inventory, level);

        boolean hasRecipe = recipe.isPresent() && outputSlotEmpty(inventory);
        if (hasRecipe) {
            entity.setHasRecipe(1);
        } else {
            entity.setHasRecipe(0);
        }
        return hasRecipe;
    }

    private static SimpleContainer getInventoryCopy(SeedSplicingStationBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        return inventory;
    }

    private static boolean sameSpecies(SimpleContainer inventory) {
        Item seed1 = inventory.getItem(0).getItem();
        Item seed2 = inventory.getItem(1).getItem();

        return seed1.equals(seed2);
    }

    private static boolean outputSlotEmpty(SimpleContainer inventory) {
        return inventory.getItem(2).isEmpty();
    }

    private boolean isCrafting() {
        return this.data.get(2) != 0;
    }

    private void setHasRecipe(int val) {
        if (val == 0) {
            this.hasRecipe = 0;
        } else {
            this.hasRecipe = 1;
        }
        setChanged(this.level, this.getBlockPos(), this.getBlockState());
    }

}
