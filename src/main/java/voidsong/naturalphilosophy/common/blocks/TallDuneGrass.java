package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.Tags;

public class TallDuneGrass extends DoublePlantBlock {

    public TallDuneGrass(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (state.getBlock() != this) return super.canSurvive(state, level, pos);
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return below.is(Tags.Blocks.SANDS) || below.is(Tags.Blocks.GRAVELS);
        } else {
            return below.getBlock() == this && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }
}
