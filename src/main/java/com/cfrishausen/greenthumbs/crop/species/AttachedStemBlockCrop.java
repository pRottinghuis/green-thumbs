package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class AttachedStemBlockCrop extends BasicCrop{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    //private final GTStemGrownBlock fruit;


    public AttachedStemBlockCrop(String name, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting/*, GTStemGrownBlock fruit*/) {
        super(name, seed, crop, cutting);
        //this.fruit = fruit;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
