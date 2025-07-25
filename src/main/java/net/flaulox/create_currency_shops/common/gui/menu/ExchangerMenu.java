package net.flaulox.create_currency_shops.common.gui.menu;

import net.flaulox.create_currency_shops.CurrencyValues;
import net.flaulox.create_currency_shops.common.blocks.entity.ExchangerBlockEntity;
import net.flaulox.create_currency_shops.common.items.CoinItem;
import net.flaulox.create_currency_shops.common.registries.CreateCurrencyShopsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.flaulox.create_currency_shops.common.registries.CreateCurrencyShopsBlocks.EXCHANGER;
import static net.flaulox.create_currency_shops.common.registries.CreateCurrencyShopsMenus.EXCHANGER_MENU;

public class ExchangerMenu extends AbstractContainerMenu {
    public final ExchangerBlockEntity blockEntity;
    private final ItemStackHandler inventory;
    private int guiYShift = 0;

    private final List<ExchangerResultSlot> outputSlots = new ArrayList<>();
    private final List<CoinInputSlot> inputSlots = new ArrayList<>();

    public ExchangerMenu(@Nullable MenuType<ExchangerMenu> exchangerMenuMenuType, int containerId, Inventory pInv, RegistryFriendlyByteBuf data) {
        this(containerId,pInv,pInv.player.level().getBlockEntity(data.readBlockPos()), new ItemStackHandler(13) {
            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return 99;
            }
        });
    }
    public ExchangerMenu(int containerId, Inventory pInv, BlockEntity blockEntity) {
        this(containerId,pInv,blockEntity, new ItemStackHandler(13) {
            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return 99;
            }
        });
    }

    public ExchangerMenu(int containerId, Inventory pInv, BlockEntity blockEntity, ItemStackHandler inventory) {
        super(EXCHANGER_MENU.get(), containerId);
        this.blockEntity = (ExchangerBlockEntity) blockEntity;
        this.inventory = inventory;

        if(pInv.player.level().isClientSide) {
            this.guiYShift = (Minecraft.getInstance().options.guiScale().get() == 5 || Minecraft.getInstance().options.guiScale().get() == 0)?30:0;
        }

        this.addInventory(pInv);
        this.addHotbar(pInv);
        this.addMenuSlots();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearResultItems();
        this.putOrDropItems(this.getContainer(),player.getInventory());
    }
    public void dropItems() {
        SimpleContainer inv = this.getContainer();
        Containers.dropContents(this.blockEntity.getLevel(),this.blockEntity.getBlockPos(),inv);
    }

    public SimpleContainer getContainer() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            container.setItem(i,inventory.getStackInSlot(i));
        }
        return container;
    }

    private void putOrDropItems(SimpleContainer container, Inventory pInv) {
        if(hasFreeSlots(pInv,getOccupiedSlots(container))) {
            container.getItems().forEach(pInv::add);
        } else {
            Containers.dropContents(pInv.player.level(),pInv.player.getOnPos(),container);
        }
    }

    private int getOccupiedSlots(SimpleContainer container) {
        int i = 0;
        for(int j = 0;j < container.getItems().size();j++) {
            if(container.getItem(i) != ItemStack.EMPTY) {
                i+=1;
            }
        }
        return i;
    }

    private void clearResultItems() {
        outputSlots.forEach(s -> s.set(ItemStack.EMPTY));
    }
    private void clearInputItems() {
        inputSlots.forEach(s -> s.set(ItemStack.EMPTY));
    }

    private void addHotbar(Inventory pInv) {
        for (int i = 0; i<9; ++i) {
            this.addSlot(new Slot(pInv,i,8+i*18,142+guiYShift));
        }
    }

    private void addInventory(Inventory pInv) {
        for (int i = 0; i<3; ++i) {
            for (int j = 0; j<9; ++j) {
                this.addSlot(new Slot(pInv,j+i*9+9,7+j*18,76+i*18+guiYShift));
            }
        }
    }

    public int getInputCost() {
        return this.getInputItems().stream().mapToInt(i -> CurrencyValues.getValue(i)*i.getCount()).sum();
    }

    public int getExchangeCount(ItemStack stack) {
        if(getInputCost() == 0 || CurrencyValues.getValue(stack) == 0) {
            return 0;
        }
        return getInputCost() / CurrencyValues.getValue(stack);
    }

    public List<ItemStack> getInputItems() {
        return inputSlots.stream().map(SlotItemHandler::getItem).toList();
    }
    public void addItemToMenu(ItemStack stack, Inventory pInv) {
        var optionalFreeSlot = this.inputSlots.stream()
                .filter(s -> !s.hasItem() || (s.getItem().getItem()==stack.getItem() && (stack.getMaxStackSize() - s.getItem().getCount()) >= stack.getCount()))
                .findFirst();
        if(optionalFreeSlot.isEmpty()) {
            if(pInv.getFreeSlot() != -1) {
                pInv.setItem(pInv.getFreeSlot(),stack);
            } else {
                Containers.dropContents(pInv.player.level(),pInv.player.getOnPos(),NonNullList.of(stack));
            }
        } else {
            this.inventory.insertItem(optionalFreeSlot.get().getSlotIndex(),stack,false);
        }
    }
    public int removeSmallestItemFromInput() {
        return this.inputSlots.stream().filter(s -> s.hasItem())
                .map(s -> CurrencyValues.getValue(s.getItem()))
                .min(Comparator.comparingInt(i -> i))
                .orElse(0);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if(clickType == ClickType.QUICK_MOVE) {
            if (this.slots.get(slotId) instanceof ExchangerResultSlot ers && this.slots.get(slotId).hasItem() && ers.mayPickup(player)) {
                ItemStack stack = this.slots.get(slotId).getItem();
                ItemStack copy = stack.copy();
                this.placeToPlayerInventory(player.getInventory(),copy,getExchangeCount(stack));
                this.clearInputAndGiveChange(getInputCost() - CurrencyValues.getValue(stack) * getExchangeCount(stack), player.getInventory());
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        final int HOTBAR_SLOT_COUNT = 9;
        final int PLAYER_INVENTORY_ROW_COUNT = 3;
        final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
        final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
        final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
        final int VANILLA_FIRST_SLOT_INDEX = 0;
        final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
        final int TE_INVENTORY_SLOT_COUNT = 6;

        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private void addMenuSlots() {
        int j = 0;
        for(int i = -5; i<=85; i+=18) {
            this.inputSlots.add((CoinInputSlot)this.addSlot(new CoinInputSlot(this.inventory,j++,i,30+guiYShift)));
        }

        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,9,12,-2+guiYShift,
                CreateCurrencyShopsItems.ZINC_COIN.asStack())));
        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,8,12,-24+guiYShift,
                CreateCurrencyShopsItems.IRON_COIN.asStack())));
        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,7,12,-46+guiYShift,
                CreateCurrencyShopsItems.COPPER_COIN.asStack())));

        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,12,98,-2+guiYShift,
                CreateCurrencyShopsItems.NETHERITE_COIN.asStack())));
        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,11,98,-24+guiYShift,
                CreateCurrencyShopsItems.GOLD_COIN.asStack())));
        outputSlots.add((ExchangerResultSlot)this.addSlot(new ExchangerResultSlot(this.inventory,10,98,-46+guiYShift,
                CreateCurrencyShopsItems.BRASS_COIN.asStack())));
    }
    public void drawCounts(GuiGraphics gui, Font font, int x, int y) {
        outputSlots.forEach(s -> s.drawCount(gui,font,x+40,y+5));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(),blockEntity.getBlockPos()),player,EXCHANGER.get());
    }

    class CoinInputSlot extends SlotItemHandler {

        public CoinInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof CoinItem;
        }
    }
    class ExchangerResultSlot extends SlotItemHandler {
        private final ItemStack initItem;

        public ExchangerResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemStack initItem) {
            super(itemHandler, index, xPosition, yPosition);
            this.initItem = initItem;
            if(!this.hasItem()) {
                this.getItemHandler().insertItem(index, initItem, false);
            }
        }

        @Override
        public void setChanged() {
        }

        @Override
        public ItemStack remove(int amount) {
            return this.hasItem()?this.initItem.copy():ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if(!player.level().isClientSide) {
                if (getExchangeCount(stack) > stack.getMaxStackSize()) {
                    placeToPlayerInventory(player.getInventory(), stack, getExchangeCount(stack));
                } else {
                    stack.setCount(getExchangeCount(stack));
                }
                clearInputAndGiveChange(getInputCost() - CurrencyValues.getValue(getItem()) * getExchangeCount(getItem()), player.getInventory());
            }
        }

        @Override
        public boolean mayPickup(Player player) {
            return getInputCost() > 0 && getExchangeCount(getItem()) > 0 && hasFreeSlots(player.getInventory(), getExchangeCount(getItem()) / getItem().getMaxStackSize());
        }

        public void drawCount(GuiGraphics gui, Font font, int x, int y) {
            for(int i = 0; i<((int)Math.log10(getExchangeCount(getItem()))); i++) {
                x-=3;
            }
            gui.drawString(font,String.valueOf(getExchangeCount(getItem())), x+this.x,y+this.y,0xffffff);
        }
    }

    private boolean hasFreeSlots(Inventory pInv, int count) {
        int j = 0;
        for(int i = 0;i < pInv.items.size();i++) {
            if(j >= count) {
                return true;
            }
            if(pInv.getItem(i) == ItemStack.EMPTY) {
                j+=1;
            }
        }
        return false;
    }

    private void placeToPlayerInventory(Inventory pInv, ItemStack stack, int i) {
        stack.setCount(i);
        pInv.add(stack);
    }

    void clearInputAndGiveChange(int change, Inventory pInv) {
        final int NETHERITE = CurrencyValues.getValue(CreateCurrencyShopsItems.NETHERITE_COIN.asStack());
        final int GOLD = CurrencyValues.getValue(CreateCurrencyShopsItems.GOLD_COIN.asStack());
        final int BRASS = CurrencyValues.getValue(CreateCurrencyShopsItems.BRASS_COIN.asStack());
        final int ZINC = CurrencyValues.getValue(CreateCurrencyShopsItems.ZINC_COIN.asStack());
        final int IRON = CurrencyValues.getValue(CreateCurrencyShopsItems.IRON_COIN.asStack());
        final int COPPER = CurrencyValues.getValue(CreateCurrencyShopsItems.COPPER_COIN.asStack());

        clearInputItems();

        int filled = 0;
        while (filled != change) {
            if(change-filled >= NETHERITE) {
                this.addItemToMenu(CreateCurrencyShopsItems.NETHERITE_COIN.asStack(),pInv);
                filled+=NETHERITE;
            } else if (change-filled >= GOLD) {
                this.addItemToMenu(CreateCurrencyShopsItems.GOLD_COIN.asStack(),pInv);
                filled+=GOLD;
            } else if (change-filled >= BRASS) {
                this.addItemToMenu(CreateCurrencyShopsItems.BRASS_COIN.asStack(),pInv);
                filled+=BRASS;
            } else if (change-filled >= ZINC) {
                this.addItemToMenu(CreateCurrencyShopsItems.ZINC_COIN.asStack(),pInv);
                filled+=ZINC;
            } else if (change-filled >= IRON) {
                this.addItemToMenu(CreateCurrencyShopsItems.IRON_COIN.asStack(),pInv);
                filled+=IRON;
            } else if (change-filled >= COPPER) {
                this.addItemToMenu(CreateCurrencyShopsItems.COPPER_COIN.asStack(),pInv);
                filled+=COPPER;
            } else if(filled > change) {
                filled-=this.removeSmallestItemFromInput();
            } else break;
        }
    }
}
