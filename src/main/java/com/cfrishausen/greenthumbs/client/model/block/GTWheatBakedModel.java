package com.cfrishausen.greenthumbs.client.model.block;

import com.cfrishausen.greenthumbs.block.entity.GTWheatBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GTWheatBakedModel implements IDynamicBakedModel {

    private final BakedModel[] bakedModels;

    public GTWheatBakedModel(BakedModel[] bakedModels) {
        this.bakedModels = bakedModels;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        if (state == null) {
            return Collections.EMPTY_LIST;
        }
        if (side != null) {
            return Collections.EMPTY_LIST;
        }

        if (extraData.has(GTWheatBlockEntity.AGE)) {
            // Pretend to be wheat block and then get quads from there
            return bakedModels[extraData.get(GTWheatBlockEntity.AGE)].getQuads(null, null, rand);
        }
        return null;
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
        return bakedModels[0].getParticleIcon();
    }

    // used by forge in place of getParticleIcion()
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        if (data.has(GTWheatBlockEntity.AGE)) {
            return bakedModels[data.get(GTWheatBlockEntity.AGE)].getParticleIcon();
        }
        return bakedModels[0].getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return null;
    }
}
