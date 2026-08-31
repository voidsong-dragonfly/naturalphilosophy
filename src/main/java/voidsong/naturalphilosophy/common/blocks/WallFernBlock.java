package voidsong.naturalphilosophy.common.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import voidsong.naturalphilosophy.common.NPTags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class WallFernBlock extends TallGrassBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(
            ImmutableMap.of(
                    Direction.NORTH,
                    Block.box(0.0, 4.0, 5.0, 16.0, 12.0, 16.0),
                    Direction.SOUTH,
                    Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 11.0),
                    Direction.WEST,
                    Block.box(5.0, 4.0, 0.0, 16.0, 12.0, 16.0),
                    Direction.EAST,
                    Block.box(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)
            )
    );

    public WallFernBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.is(NPTags.Blocks.SUPPORTS_WALL_FERN);
    }

    @Override
    @Nonnull
    protected BlockState updateShape(BlockState state, Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        return facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos wallPos = pos.relative(direction.getOpposite());
        BlockState wallState = level.getBlockState(wallPos);
        return wallState.isFaceSturdy(level, wallPos, direction) && mayPlaceOn(wallState, level, wallPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal() && state != null) {
                state = state.setValue(FACING, direction.getOpposite());
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return state;
                }
            }
        }

        return null;
    }

    @Override
    @Nonnull
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
