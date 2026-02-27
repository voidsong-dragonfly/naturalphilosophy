package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import voidsong.naturalphilosophy.common.NPTags;

import javax.annotation.Nonnull;

public class WaterPlantBlock extends DoublePlantBlock implements SimpleWaterloggedBlock, BonemealableBlock {

    public WaterPlantBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(BlockStateProperties.WATERLOGGED))
            return false;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState below = level.getBlockState(pos.below());
            boolean fluid = level.getFluidState(pos).is(Fluids.WATER);
            if(level.getFluidState(pos).isEmpty())
                for(BlockPos search : BlockPos.betweenClosed(pos.offset(-3, -1, -3), pos.offset(3, -1, 3)))
                    fluid = fluid || level.getFluidState(search).is(Fluids.WATER);
            return below.is(NPTags.Blocks.WATER_PLANTS) && fluid;
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    @Nonnull
    public BlockState updateShape(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            if(!canSurvive(state, level, pos)) return Blocks.WATER.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null)
            return state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
       return null;
    }

    @Override
    @Nonnull
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockPos center = state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? pos.below() : pos;
        for (BlockPos search : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
            boolean water = level.getBlockState(search).is(Blocks.WATER);
            boolean bottomValid = this.canSurvive(this.defaultBlockState(), level, search) && (level.getBlockState(search).isAir() || water);
            boolean topValid = level.getBlockState(search.above()).isAir();
            if (bottomValid && topValid && random.nextInt(5) == 0) {
                level.setBlockAndUpdate(search, defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, water));
                level.setBlockAndUpdate(search.above(), defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
            }
        }
    }
}
