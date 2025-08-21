package net.flaulox.create_currency_shops.common.blocks.entity;

import net.flaulox.create_currency_shops.common.gui.menu.ExchangerMenu;
import net.flaulox.create_currency_shops.common.registries.CreateCurrencyShopsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ExchangerBlockEntity extends BlockEntity implements MenuProvider {

    public ExchangerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        this(pos, blockState);
    }

    public ExchangerBlockEntity(BlockPos pos, BlockState blockState) {
        super(CreateCurrencyShopsBlockEntities.EXCHANGER.get(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.create_currency_shops.exchanger.gui_name");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ExchangerMenu(i,inventory,this);
    }
}
