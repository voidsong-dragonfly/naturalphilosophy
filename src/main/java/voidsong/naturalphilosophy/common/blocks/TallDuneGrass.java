package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.PlantType;

import javax.annotation.Nonnull;

public class TallDuneGrass extends DoublePlantBlock {

    public TallDuneGrass(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NPProperties.RED_SAND, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NPProperties.RED_SAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (state.getBlock() != this) return super.canSurvive(state, level, pos);
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return state.getValue(NPProperties.RED_SAND) ? below.is(Blocks.RED_SAND) : below.is(Blocks.SAND);
        } else {
            return below.getBlock() == this && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    @Nonnull
    public PlantType getPlantType(@Nonnull BlockGetter world, @Nonnull BlockPos pos) {
        return PlantType.DESERT;
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return context.getLevel().getBlockState(context.getClickedPos().below()).is(Blocks.RED_SAND) ? state.setValue(NPProperties.RED_SAND, true) : state;
    }
}
