package net.flaulox.create_currency_shops;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@EventBusSubscriber(modid = CreateCurrencyShops.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();





    private static final ModConfigSpec.ConfigValue<String> LOWEST_DENOMINATION_STRING =
            BUILDER.comment("Item that serves as the lowest denomination with the value of 1 in the format 'modid:item=value'")
                    .define("lowestDenomination", "create_currency_shops:copper_coin", Config::validateItemName);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> CURRENCY_VALUES_STRINGS =
            BUILDER.comment("A list of Items and their values in the format 'modid:item=value'")
                    .defineListAllowEmpty("currencyValues", List.of("create_currency_shops:iron_coin=5","create_currency_shops:zinc_coin=10","create_currency_shops:brass_coin=50","create_currency_shops:gold_coin=100","create_currency_shops:netherite_coin=500"), Config::currencyValuesEntry);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static Map<Item, Integer> currencyValues;






    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {






        currencyValues = CURRENCY_VALUES_STRINGS.get().stream()
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(
                        parts -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(parts[0])),
                        parts -> Integer.parseInt(parts[1])
                ));


        currencyValues.put(BuiltInRegistries.ITEM.get(ResourceLocation.parse(LOWEST_DENOMINATION_STRING.get())), 1);


    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName
                && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static boolean currencyValuesEntry(final Object obj) {
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
