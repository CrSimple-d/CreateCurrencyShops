package net.flaulox.create_currency_shops.common.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.flaulox.create_currency_shops.common.gui.menu.ExchangerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipnext.ingame.InventoryKt;

public class ExchangerScreen extends AbstractContainerScreen<ExchangerMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCurrencyShops.MOD_ID,"textures/gui/exchanger/exchanger_gui.png");

    private int guiYShift;

    public ExchangerScreen(ExchangerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0,GUI_TEXTURE);
        int x = (width - imageWidth)/2-13;
        int y = (height - imageHeight)/2-(76-guiYShift);
        guiGraphics.blit(GUI_TEXTURE,x,y,0,0,imageWidth+25,imageHeight+90);
    }

    private void renderCost(GuiGraphics guiGraphics) {
        int input = this.menu.getInputCost();
        guiGraphics.drawString(this.font, String.valueOf(input), (width - imageWidth)/2+106, (height - imageHeight)/2+(30+guiYShift),0xfbec5d);
        guiGraphics.drawString(this.font,Component.translatable("create_currency_shops.name_of_currency"), (width - imageWidth)/2+106, (height - imageHeight)/2+(37+guiYShift),0xfbec5d);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+52, this.titleLabelY-(77-guiYShift), 0xffd700);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX+50, this.inventoryLabelY-(13-guiYShift), 0x166866, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.guiYShift = this.minecraft != null && (this.minecraft.options.guiScale().get() == 5 || Minecraft.getInstance().options.guiScale().get() == 0)?30:0;
        this.renderCost(guiGraphics);
        this.menu.drawCounts(guiGraphics,this.font,(width - imageWidth)/2,(height - imageHeight)/2);
        RenderSystem.enableDepthTest();
        this.renderTooltip(guiGraphics,mouseX,mouseY);
    }
}
