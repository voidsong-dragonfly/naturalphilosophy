package voidsong.naturalphilosophy.common.worldgen.features;


import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public class FallingSpringFeature extends Feature<SpringConfiguration> {
    private int SEARCH_HEIGHT = 32;

    public FallingSpringFeature(Codec<SpringConfiguration> configurationCodec) {
        super(configurationCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpringConfiguration> config) {
        SpringConfiguration springConfig = config.config();
        WorldGenLevel worldgenlevel = config.level();
        BlockPos blockpos = config.origin();
        if (!worldgenlevel.getBlockState(blockpos.above()).is(springConfig.validBlocks)||!worldgenlevel.getBlockState(blockpos.above(2)).is(springConfig.validBlocks)) {
            return false;
        } else if (springConfig.requiresBlockBelow && !worldgenlevel.getBlockState(blockpos.below()).is(springConfig.validBlocks)) {
            return false;
        } else {
            BlockState blockstate = worldgenlevel.getBlockState(blockpos);
            if (!blockstate.isAir() && !blockstate.is(springConfig.validBlocks)) {
                return false;
            } else {
                int i = 0;
                int j = 0;
                for(Direction direction : Direction.Plane.HORIZONTAL) {
                    if (worldgenlevel.getBlockState(blockpos.relative(direction)).is(springConfig.validBlocks)) {
                        j++;
                    }
                }
                if (worldgenlevel.getBlockState(blockpos.below()).is(springConfig.validBlocks)) {
                    j++;
                }

                BlockPos hole = blockpos;
                int k = 0;
                for(Direction direction : Direction.Plane.HORIZONTAL) {
                    if (worldgenlevel.isEmptyBlock(blockpos.relative(direction))) {
                        hole = blockpos.relative(direction);
                        k++;
                    }
                }
                if (worldgenlevel.isEmptyBlock(blockpos.below())) {
                    hole = blockpos.below();
                    k++;
                }

                if (j >= springConfig.rockCount && k <= springConfig.holeCount && k > 0) {
                    int search = 1;
                    boolean success = false;
                    while(worldgenlevel.isEmptyBlock(hole.below(search))&&search<SEARCH_HEIGHT) {
                        search++;
                        if(worldgenlevel.getFluidState(hole.below(search)).is(springConfig.state.getType())) {
                            success = true;
                            break;
                        }
                    }
                    if(success) {
                        worldgenlevel.setBlock(blockpos, springConfig.state.createLegacyBlock(), 2);
                        worldgenlevel.scheduleTick(blockpos, springConfig.state.getType(), 0);
                        i++;
                    }
                }
                return i > 0;
            }
        }
    }
}
