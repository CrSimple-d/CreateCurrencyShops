package net.flaulox.create_currency_shops.common.blocks;

import com.mojang.serialization.MapCodec;
import net.flaulox.create_currency_shops.common.blocks.entity.ExchangerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exchanger extends BaseEntityBlock {
    public static final EnumProperty<ExchangerParts> PART = EnumProperty.create("part",ExchangerParts.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<Exchanger> CODEC = simpleCodec(Exchanger::new);
    private static final VoxelShape SHAPE = Block.box(0d,0d,3d,16d,15d,16d);

    public Exchanger(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(PART,ExchangerParts.MASTER)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ExchangerBlockEntity(blockPos,blockState);
    }

    public enum ExchangerParts implements StringRepresentable {
        MASTER("master"),
        ADDITIONAL("additional");

        private final String name;

        ExchangerParts(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(!level.isClientSide) {
            removeAll(level, pos, getRemovePositions(pos,state));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void removeAll(Level level, BlockPos pos, List<BlockPos> locs) {
        for (BlockPos loc : locs) {
            if(!loc.equals(pos) && level.getBlockState(loc).getBlock() == this) {
                level.removeBlock(loc,false);
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!level.isClientSide) {
            BlockPos[] locs = getPositions(pos);
            if(hasNotSpace(locs, level, pos)) {
                level.removeBlock(pos,false);
                if(placer instanceof Player player) {
                    player.displayClientMessage(Component.translatable("block.create_currency_shops.exchanger.not_enough_space")
                            .withColor(0xFF0000),true);
                }
                return;
            }

            for (BlockPos loc : locs) {
                if(!loc.equals(pos)) {
                    level.setBlockAndUpdate(loc,state.setValue(PART,ExchangerParts.ADDITIONAL));
                }
            }
        }
    }

    private List<BlockPos> getRemovePositions(BlockPos pos, BlockState state) {
        List<BlockPos> blist = new ArrayList<>();
        if(state.getValue(PART) == ExchangerParts.ADDITIONAL) {
            blist.add(pos);
            blist.add(pos.offset(0, -1, 0));
        } else {
            blist = List.of(getPositions(pos));
        }
        return blist;
    }

    private BlockPos[] getPositions(BlockPos pos) {
        return new BlockPos[]{pos.offset(0,1,0),pos};
    }

    private boolean hasNotSpace(BlockPos[] locs, Level level, BlockPos except) {
        for (BlockPos loc : locs) {
            if(loc != except && level.getBlockState(loc).getBlock() != Blocks.AIR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof ExchangerBlockEntity blockEntity) {
            this.openMenu(pos,player,blockEntity);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    private void openMenu(BlockPos pos, Player player, ExchangerBlockEntity blockEntity) {
        player.openMenu(new SimpleMenuProvider(blockEntity,Component.translatable("block.create_currency_shops.exchanger.gui_name")),pos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART,FACING);
    }
}
