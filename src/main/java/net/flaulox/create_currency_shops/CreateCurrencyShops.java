package net.flaulox.create_currency_shops;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.flaulox.create_currency_shops.common.gui.screen.ExchangerScreen;
import net.flaulox.create_currency_shops.common.registries.*;
import net.flaulox.create_currency_shops.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Field;

@Mod(CreateCurrencyShops.MOD_ID)
public class CreateCurrencyShops {
    public static final String MOD_ID = "create_currency_shops";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateCurrencyShopsRegistrate CREATE_CURRENCY_SHOPS_REGISTRATE = CreateCurrencyShopsRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    static {
        CREATE_CURRENCY_SHOPS_REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(create(item))));
    }

    public CreateCurrencyShops(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        CREATE_CURRENCY_SHOPS_REGISTRATE.registerEventListeners(modEventBus);

        CreateCurrencyShopsItems.register();
        CreateCurrencyShopsBlocks.register();
        CreateCurrencyShopsMenus.register();
        CreateCurrencyShopsBlockEntities.register(modEventBus);
        CreateCurrencyShopsCreativeModeTab.register(modEventBus);
        NeoForge.EVENT_BUS.register(TooltipHandler.class);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public static CreateCurrencyShopsRegistrate registrate() {
        return CREATE_CURRENCY_SHOPS_REGISTRATE;
    }

    @Nullable
    public static KineticStats create(Item item) {
        return null;
    }

    @EventBusSubscriber(modid = MOD_ID,bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(FMLClientSetupEvent e) {
            ItemBlockRenderTypes.setRenderLayer(CreateCurrencyShopsBlocks.EXCHANGER.get(), RenderType.SOLID);
        }
    }
}
