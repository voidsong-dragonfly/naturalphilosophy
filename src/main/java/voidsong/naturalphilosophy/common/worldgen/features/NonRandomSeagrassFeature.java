package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class NonRandomSeagrassFeature extends Feature<ProbabilityFeatureConfiguration> {
    public NonRandomSeagrassFeature(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> config) {
        boolean success = false;
        WorldGenLevel level = config.level();
        BlockPos pos = config.origin();
        int k = level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ());
        BlockPos floorPos = k > level.getSeaLevel() ? pos : new BlockPos(pos.getX(), k, pos.getZ());
        if (level.getBlockState(floorPos).is(Blocks.WATER)) {
            boolean flag = config.random().nextDouble() < (double)config.config().probability;
            BlockState state = flag ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
            if (state.canSurvive(level, floorPos)) {
                if (flag) {
                    BlockPos topPos = floorPos.above();
                    if (level.getBlockState(topPos).is(Blocks.WATER)) {
                        level.setBlock(floorPos, state, 2);
                        level.setBlock(topPos, state.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER), 2);
                    }
                } else {
                    level.setBlock(floorPos, state, 2);
                }
                success = true;
            }
        }
        return success;
    }
}
