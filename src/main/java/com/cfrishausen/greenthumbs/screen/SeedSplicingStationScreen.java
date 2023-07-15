package com.cfrishausen.greenthumbs.screen;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SeedSplicingStationScreen extends AbstractContainerScreen<SeedSplicingStationMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreenThumbs.ID,"textures/gui/container/seed_splicing_station_gui.png");

    public SeedSplicingStationScreen(SeedSplicingStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 180;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isHovering(132, 65, 19, 8, mouseX, mouseY) && this.menu.clickMenuButton(this.minecraft.player, 0)) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Hover on start button
        if (isHovering(132, 65, 19, 8, mouseX, mouseY)) {
            blit(poseStack, x + 132, y + 65, 176, 31, 20, 10);
        }

        renderProgressArrow(poseStack, x, y);
    }

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting()) {

            // (posStack, x of draw, y of draw, x where to get, y where to get, width of what getting, height of what getting)
            // move colored strand right
            blit(pPoseStack, x + 55, y + 36, 60 - menu.getScaledProgress(), 204, menu.getScaledProgress(), 24);
            // move grey strand right
            blit(pPoseStack, x + 55 + menu.getScaledProgress(), y + 36, 0, 180, 60 - menu.getScaledProgress(), 24);
        } else {
            // Grey strand
            blit(pPoseStack, x + 55, y + 36, 0, 180, 60, 24);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);

        if (isHovering(134, 40, 16, 16, mouseX, mouseY)) {

        }

    }
}
