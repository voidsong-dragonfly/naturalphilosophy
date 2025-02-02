package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;

public class DuneGrass extends TallGrassBlock {

    public DuneGrass(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NPProperties.RED_SAND, false));
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
        return state.getValue(NPProperties.RED_SAND) ? below.is(Blocks.RED_SAND) : below.is(Blocks.SAND);
    }

    @Override
    public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        DoublePlantBlock doubleplantblock = (DoublePlantBlock) NPBlocks.TALL_DUNE_GRASS.get();
        if (doubleplantblock.defaultBlockState().canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
            DoublePlantBlock.placeAt(level, doubleplantblock.defaultBlockState().setValue(NPProperties.RED_SAND, state.getValue(NPProperties.RED_SAND)), pos, 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return context.getLevel().getBlockState(context.getClickedPos().below()).is(Blocks.RED_SAND) ? state.setValue(NPProperties.RED_SAND, true) : state;
    }

}
