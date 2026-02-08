package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public class GiantBambooLeavesBlock extends LeavesBlock {
    public GiantBambooLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /*
     * The below methods are copied in their entirety from LeavesBlock, except using this class' distance function
     */

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockState blockstate = this.defaultBlockState()
            .setValue(PERSISTENT, true)
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return updateDistance(blockstate, context.getLevel(), context.getClickedPos());
    }

    @Override
    @Nonnull
    protected BlockState updateShape(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        int i = getDistanceAt(facingState) + 1;
        if (i != 1 || state.getValue(DISTANCE) != i) {
            level.scheduleTick(currentPos, this, 1);
        }

        return state;
    }

    @Override
    protected void tick(@Nonnull BlockState state, ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        level.setBlock(pos, updateDistance(state, level, pos), 3);
    }

    /*
     * The below methods are copied in their entirety from LeavesBlock, with one change:
     * The distance referred to is now distance from a GiantBambooStalkBlock and not BlockTags.LOGS
     */

    private static BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = 7;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(pos, direction);
            i = Math.min(i, getDistanceAt(level.getBlockState(blockpos$mutableblockpos)) + 1);
            if (i == 1) {
                break;
            }
        }

        return state.setValue(DISTANCE, i);
    }

    private static int getDistanceAt(BlockState neighbor) {
        return getOptionalDistanceAt(neighbor).orElse(7);
    }

    @Nonnull
    public static OptionalInt getOptionalDistanceAt(BlockState state) {
        if (state.is(NPBlocks.GIANT_BAMBOO)) {
            return OptionalInt.of(0);
        } else {
            return state.hasProperty(DISTANCE) ? OptionalInt.of(state.getValue(DISTANCE)) : OptionalInt.empty();
        }
    }
}
