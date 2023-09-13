package com.cfrishausen.greenthumbs.item.custom.client;

import com.cfrishausen.greenthumbs.crop.state.CropState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.Map;

public interface ICropSpeciesExtensions {

    Map<CropState, ModelResourceLocation> getModelMap();

}
