package net.flaulox.create_currency_shops.config;

import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.config.ModConfig;              // <-- импорт нужного ModConfig.Type
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = CreateCurrencyShops.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<String> LOWEST_DENOMINATION_STRING =
            BUILDER.comment("Item that serves as the lowest denomination with the value of 1 in the format 'modid:item=value'")
                    .define("lowestDenomination", "create_currency_shops:copper_coin", Config::validateItemName);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> CURRENCY_VALUES_STRINGS =
            BUILDER.comment("A list of Items and their values in the format 'modid:item=value'")
                    .defineListAllowEmpty("currencyValues",
                            List.of(
                                    "create_currency_shops:iron_coin=10",
                                    "create_currency_shops:zinc_coin=50",
                                    "create_currency_shops:brass_coin=200",
                                    "create_currency_shops:gold_coin=1000",
                                    "create_currency_shops:netherite_coin=5000"
                            ),
                            Config::currencyValuesEntry
                    );

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static Map<Item, Integer> currencyValues;

    /** При первой загрузке конфига */
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            applyConfig();
        }
    }

    /** При /reload */
    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            applyConfig();
        }
    }

    private static void applyConfig() {
        currencyValues = CURRENCY_VALUES_STRINGS.get().stream()
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(
                        parts -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(parts[0])),
                        parts -> Integer.parseInt(parts[1])
                ));

        currencyValues.put(
                BuiltInRegistries.ITEM.get(ResourceLocation.parse(LOWEST_DENOMINATION_STRING.get())),
                1
        );
    }

    private static boolean validateItemName(Object obj) {
        if (!(obj instanceof String itemName)) return false;
        ResourceLocation loc = ResourceLocation.parse(itemName);
        return BuiltInRegistries.ITEM.containsKey(loc);
    }

    private static boolean currencyValuesEntry(Object obj) {
        if (!(obj instanceof String entry)) return false;
        String[] parts = entry.split("=");
        if (parts.length != 2) return false;
        try {
            ResourceLocation itemId = ResourceLocation.parse(parts[0]);
            int price = Integer.parseInt(parts[1]);
            return price >= 0 && BuiltInRegistries.ITEM.containsKey(itemId);
        } catch (Exception e) {
            return false;
        }
    }
}
