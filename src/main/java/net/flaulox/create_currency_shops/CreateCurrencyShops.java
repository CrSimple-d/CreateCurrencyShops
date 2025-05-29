package net.flaulox.create_currency_shops;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.Nullable;

@Mod(CreateCurrencyShops.MODID)
public class CreateCurrencyShops {
    public static final String MODID = "create_currency_shops";

    public static final CreateCurrencyShopsRegistrate CREATE_CURRENCY_SHOPS_REGISTRATE = CreateCurrencyShopsRegistrate.create(MODID)
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
        CreateCurrencyShopsCreativeModeTab.register(modEventBus);
        NeoForge.EVENT_BUS.register(TooltipHandler.class);


        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }



    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static CreateCurrencyShopsRegistrate registrate() {

        return CREATE_CURRENCY_SHOPS_REGISTRATE;
    }

    @Nullable
    public static KineticStats create(Item item) {
        return null;
    }
}
