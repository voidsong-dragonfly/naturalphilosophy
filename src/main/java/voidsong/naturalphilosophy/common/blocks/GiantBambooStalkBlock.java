package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.NPTags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GiantBambooStalkBlock extends Block implements BonemealableBlock {
    protected static final VoxelShape SMALL_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape LARGE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

    public GiantBambooStalkBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any().setValue(AGE, 0).setValue(LEAVES, BambooLeaves.NONE).setValue(STAGE, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, LEAVES, STAGE);
    }

    @Override
    protected int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.hasProperty(LEAVES) && !state.getValue(LEAVES).equals(BambooLeaves.NONE) ? 1 : 0;
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        VoxelShape voxelshape = state.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
        Vec3 vec3 = state.getOffset(level, pos);
        return voxelshape.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    @Nonnull
    protected VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return COLLISION_SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    protected boolean isCollisionShapeFullBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (!fluidstate.isEmpty()) {
            return null;
        } else {
            BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
            if ( below.is(NPTags.Blocks.GIANT_BAMBOO_PLANTABLE_ON)) {
                if (below.is(NPBlocks.GIANT_BAMBOO_SAPLING)) {
                    return this.defaultBlockState().setValue(AGE, 0);
                } else if (below.is(NPBlocks.GIANT_BAMBOO)) {
                    int i = below.getValue(AGE) > 0 ? 1 : 0;
                    return this.defaultBlockState().setValue(AGE, i);
                } else {
                    BlockState above = context.getLevel().getBlockState(context.getClickedPos().above());
                    return above.is(NPBlocks.GIANT_BAMBOO) ? this.defaultBlockState().setValue(AGE, above.getValue(AGE)) : NPBlocks.GIANT_BAMBOO_SAPLING.get().defaultBlockState();
                }
            } else {
                return null;
            }
        }
    }

    @Override
    protected void tick(BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(STAGE) == 0;
    }

    @Override
    protected void randomTick(BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            if (level.isEmptyBlock(pos.above()) && level.getRawBrightness(pos.above(), 0) >= 9) {
                int i = this.getHeightBelowUpToMax(level, pos) + 1;
                if (i < 16 && CommonHooks.canCropGrow(level, pos, state, random.nextInt(3) == 0)) {
                    this.growBamboo(state, level, pos, random, i);
                    CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    protected boolean canSurvive(@Nonnull BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(NPTags.Blocks.GIANT_BAMBOO_PLANTABLE_ON);
    }

    @Override
    @Nonnull
    protected BlockState updateShape(BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }

        if (direction == Direction.UP && neighborState.is(NPBlocks.GIANT_BAMBOO) && neighborState.getValue(AGE) > state.getValue(AGE)) {
            level.setBlock(pos, state.cycle(AGE), 2);
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int i = this.getHeightAboveUpToMax(level, pos);
        int j = this.getHeightBelowUpToMax(level, pos);
        return i + j + 1 < 16 && level.getBlockState(pos.above(i)).getValue(STAGE) != 1;
    }

    @Override
    public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@Nonnull ServerLevel level, RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int i = this.getHeightAboveUpToMax(level, pos);
        int j = this.getHeightBelowUpToMax(level, pos);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);

        for (int i1 = 0; i1 < l; i1++) {
            BlockPos blockpos = pos.above(i);
            BlockState blockstate = level.getBlockState(blockpos);
            if (k >= 16 || blockstate.getValue(STAGE) == 1 || !level.isEmptyBlock(blockpos.above())) {
                return;
            }

            this.growBamboo(blockstate, level, blockpos, random, k);
            i++;
            k++;
        }
    }

    protected void growBamboo(BlockState state, Level level, BlockPos pos, RandomSource random, int age) {
        BlockState below = level.getBlockState(pos.below());
        BlockPos blockpos = pos.below(2);
        BlockState belowState = level.getBlockState(blockpos);
        BambooLeaves leaves = BambooLeaves.NONE;
        if (age >= 1) {
            if (!below.is(NPBlocks.GIANT_BAMBOO) || below.getValue(LEAVES) == BambooLeaves.NONE) {
                leaves = BambooLeaves.SMALL;
            } else if (below.is(NPBlocks.GIANT_BAMBOO) && below.getValue(LEAVES) != BambooLeaves.NONE) {
                leaves = BambooLeaves.LARGE;
                if (belowState.is(NPBlocks.GIANT_BAMBOO)) {
                    level.setBlock(pos.below(), below.setValue(LEAVES, BambooLeaves.SMALL), 3);
                    level.setBlock(blockpos, belowState.setValue(LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }

        int i = state.getValue(AGE) != 1 && !belowState.is(NPBlocks.GIANT_BAMBOO) ? 0 : 1;
        int j = (age < 11 || !(random.nextFloat() < 0.25F)) && age != 15 ? 0 : 1;
        level.setBlock(
            pos.above(), this.defaultBlockState().setValue(AGE, i).setValue(LEAVES, leaves).setValue(STAGE, j), 3
        );
        if (j == 1) {
            // Place the rounded blob top part
            for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 3, 2))) {
                int height = neighborPos.getY() - (pos.getY() + 2);
                int radius = Math.max(1 + -(height)/2, 0);
                int x_spacing = Math.abs(neighborPos.getX() - pos.getX());
                int z_spacing = Math.abs(neighborPos.getZ() - pos.getZ());
                if (level.getBlockState(neighborPos).canBeReplaced() && !this.shouldSkipLeafLocation(random, x_spacing, height, z_spacing, radius) && (x_spacing <= radius && z_spacing <= radius)) {
                    level.setBlockAndUpdate(neighborPos, NPBlocks.GIANT_BAMBOO_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, Math.max(1, x_spacing + z_spacing)));
                }
            }
            // Place the final ring that steps back in
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos bottom = pos.below(2);
                level.setBlockAndUpdate(bottom.relative(direction), NPBlocks.GIANT_BAMBOO_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1));
            }
            // Properly update the leaf sizing for the bamboo below
            level.setBlock(pos.below(2), below.setValue(LEAVES, BambooLeaves.LARGE), 3);
            level.setBlock(pos.below(3), below.setValue(LEAVES, BambooLeaves.SMALL), 3);
        }
    }

    protected boolean shouldSkipLeafLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range) {
        boolean notFirstStep = !((localY == -1 && range <= 1) || (localY == 0 && (random.nextInt(3) == 0)));
        boolean corners = localX == range && localZ == range || (range >= 3 && (localX + localZ > (2*range - 2)));
        return notFirstStep && corners;
    }

    protected int getHeightAboveUpToMax(BlockGetter level, BlockPos pos) {
        int i = 0;

        while (i < 16 && level.getBlockState(pos.above(i + 1)).is(NPBlocks.GIANT_BAMBOO)) {
            i++;
        }

        return i;
    }

    protected int getHeightBelowUpToMax(BlockGetter level, BlockPos pos) {
        int i = 0;

        while (i < 16 && level.getBlockState(pos.below(i + 1)).is(NPBlocks.GIANT_BAMBOO)) {
            i++;
        }

        return i;
    }
}
