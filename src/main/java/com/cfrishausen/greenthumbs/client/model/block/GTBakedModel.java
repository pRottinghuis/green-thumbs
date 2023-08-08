package com.cfrishausen.greenthumbs.client.model.block;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GTBakedModel implements IDynamicBakedModel {

    private final Map<CropState, BakedModel> bakedModels = new HashMap<>();

    public void addModels(Map<CropState, BakedModel> bakedModelMap) {
        this.bakedModels.putAll(bakedModelMap);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        if (state == null) {
            return Collections.emptyList();
        }
        if (side != null) {
            return Collections.emptyList();
        }
        if (data.has(GTCropBlockEntity.CROP_STATE)) {
            BakedModel bakedModel = bakedModels.get(data.get(GTCropBlockEntity.CROP_STATE));
            return bakedModel == null ? Collections.emptyList() : bakedModel.getQuads(null, null, rand, ModelData.EMPTY, null);

        }
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    // not used by forge
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
    }

    // used by forge in place of getParticleIcion()
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        if (data.has(GTCropBlockEntity.AGE) && data.has(GTCropBlockEntity.CROP_TYPE)) {
            return bakedModels.get(data.get(GTCropBlockEntity.CROP_STATE)).getParticleIcon();
        }
        return Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return null;
    }
}
