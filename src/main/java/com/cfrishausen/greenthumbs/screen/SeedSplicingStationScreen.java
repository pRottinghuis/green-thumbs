package com.cfrishausen.greenthumbs.screen;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SeedSplicingStationScreen extends AbstractContainerScreen<SeedSplicingStationMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreenThumbs.ID,"textures/gui/container/seed_splicing_station_gui.png");


    public SeedSplicingStationScreen(SeedSplicingStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pPoseStack, x, y);
    }

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting()) {
            // (posStack, x of draw, y of draw, x where to get, y where to get, width of what getting, height of what getting)
            // Height width of drawing changes as the progress of the recipe continues
            blit(pPoseStack, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 17);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
