package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class WaterPlantBlock extends DoublePlantBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    public static final BooleanProperty WET = BooleanProperty.create("wet");

    public WaterPlantBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, false).setValue(WET, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED, WET);
    }

    @Override
    public boolean canSurvive(BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(BlockStateProperties.WATERLOGGED))
            return false;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return level.getBlockState(pos.below()).is(Blocks.MUD) && (level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).is(state.getBlock()));
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    @Nonnull
    public BlockState updateShape(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
            return state.setValue(BlockStateProperties.WATERLOGGED, water).setValue(WET, water);
        }
       return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, copyWaterloggedFrom(level, blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)).setValue(WET, state.getValue(WET)), 3);
    }

    @Override
    @Nonnull
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
