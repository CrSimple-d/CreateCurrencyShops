package net.flaulox.create_currency_shops.common.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.flaulox.create_currency_shops.CreateCurrencyShops;
import net.flaulox.create_currency_shops.common.blocks.entity.ExchangerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreateCurrencyShopsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateCurrencyShops.MOD_ID);

    public static final Supplier<BlockEntityType<ExchangerBlockEntity>> EXCHANGER =
            BLOCK_ENTITIES.register("pedestal_be", () -> BlockEntityType.Builder.of(
                    ExchangerBlockEntity::new, CreateCurrencyShopsBlocks.EXCHANGER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
