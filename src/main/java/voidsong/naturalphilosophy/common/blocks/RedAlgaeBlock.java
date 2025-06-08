package voidsong.naturalphilosophy.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedAlgaeBlock extends BushBlock implements BonemealableBlock, LiquidBlockContainer {
    public static final MapCodec<RedAlgaeBlock> CODEC = simpleCodec(RedAlgaeBlock::new);
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    public static final BooleanProperty LARGE = BooleanProperty.create("large");

    @Override
    @Nonnull
    public MapCodec<RedAlgaeBlock> codec() {
        return CODEC;
    }

    public RedAlgaeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LARGE, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LARGE);
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos) {
        return state.isFaceSturdy(getter, pos, Direction.UP) && !state.is(Blocks.MAGMA_BLOCK);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState state = context.getLevel().getFluidState(context.getClickedPos());
        return state.is(FluidTags.WATER) && state.getAmount() == 8 ? super.getStateForPlacement(context) : null;
    }

    @Override
    @Nonnull
    protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction dir, @Nonnull BlockState state2, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos pos2) {
        BlockState blockstate = super.updateShape(state, dir, state2, level, pos, pos2);
        if (!blockstate.isAir())
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return blockstate;
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return !state.getValue(LARGE);
    }

    @Override
    public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return !state.getValue(LARGE);
    }

    @Override
    @Nonnull
    protected FluidState getFluidState(@Nonnull BlockState state) {
        return Fluids.WATER.getSource(false);
    }

    @Override
    public void performBonemeal(ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        level.setBlockAndUpdate(pos, state.setValue(LARGE, true));
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(@Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull FluidState fluid) {
        return false;
    }
}