package com.cfrishausen.greenthumbs.screen;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import java.util.List;

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
        if(isHovering(117, 70, 50, 15, mouseX, mouseY) && this.menu.clickMenuButton(this.minecraft.player, 0)) {
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

        this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.hasRecipe()) {
            // show seed silhouette
            blit(poseStack, leftPos + 134, topPos + 40, 176, 45, 16, 16);

            // Show button in ready to activate state
            blit(poseStack, leftPos + 117, topPos + 70, 176, 30, 50, 15);

            // Hover on splice button
            if (isHovering(117, 70, 50, 15, mouseX, mouseY)) {
                if (!this.menu.isCrafting()) {
                    blit(poseStack, leftPos + 117, topPos + 70, 176, 0, 50, 15);
                }
            }

            // Show pressed if crafting otherwise show hover
            if (this.menu.isCrafting()) {
                blit(poseStack, leftPos + 117, topPos + 70, 176, 0, 50, 15);
            } else {
                if (isHovering(117, 70, 50, 15, mouseX, mouseY)) {
                    blit(poseStack, leftPos + 117, topPos + 70, 176, 15, 50, 15);
                }
            }


            // show lit up xp cost if player in creative or has enough xp or during crafting
            if (this.minecraft.player.getAbilities().instabuild || this.minecraft.player.experienceLevel >= 3 || this.menu.isCrafting()) {
                blit(poseStack, leftPos + 151, topPos + 72, 176, 61, 13, 11);
            }

        }

        renderProgressArrow(poseStack);
    }

    private void renderProgressArrow(PoseStack pPoseStack) {
        if(menu.isCrafting()) {

            // (posStack, x of draw, y of draw, x where to get, y where to get, width of what getting, height of what getting)
            // move colored strand right
            blit(pPoseStack, leftPos + 55, topPos + 36, 60 - menu.getScaledProgress(), 204, menu.getScaledProgress(), 24);
            // move grey strand right
            blit(pPoseStack, leftPos + 55 + menu.getScaledProgress(), topPos + 36, 0, 180, 60 - menu.getScaledProgress(), 24);
        } else {
            // Grey strand
            blit(pPoseStack, leftPos + 55, topPos + 36, 0, 180, 60, 24);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
        if (this.menu.hasRecipe() && isHovering(134, 40, 16, 16, mouseX, mouseY)) {
            List<Component> componentList = this.menu.getToolTipList();
            this.renderComponentTooltip(poseStack, componentList, mouseX, mouseY);
        }
    }
}
