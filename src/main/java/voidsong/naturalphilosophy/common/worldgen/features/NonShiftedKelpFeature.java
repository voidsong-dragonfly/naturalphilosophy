package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NonShiftedKelpFeature extends Feature<NoneFeatureConfiguration> {
    public NonShiftedKelpFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config) {
        int i = 0;
        WorldGenLevel level = config.level();
        BlockPos pos = config.origin();
        RandomSource random = config.random();
        int j = level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ());
        BlockPos current = j > level.getSeaLevel() ? pos : new BlockPos(pos.getX(), j, pos.getZ());
        if (level.getBlockState(current.below()).is(BlockTags.ICE) && j > pos.getY())
            current = pos;
        if (level.getBlockState(current).is(Blocks.WATER)) {
            BlockState kelpState = Blocks.KELP.defaultBlockState();
            BlockState kelpPlantState = Blocks.KELP_PLANT.defaultBlockState();
            int k = 1 + random.nextInt(10);

            for (int l = 0; l <= k; l++) {
                if (level.getBlockState(current).is(Blocks.WATER) && level.getBlockState(current.above()).is(Blocks.WATER) && kelpPlantState.canSurvive(level, current)) {
                    if (l == k) {
                        level.setBlock(current, kelpState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        i++;
                    } else {
                        level.setBlock(current, kelpPlantState, 2);
                    }
                } else if (l > 0) {
                    BlockPos floorState = current.below();
                    if (kelpState.canSurvive(level, floorState) && !level.getBlockState(floorState.below()).is(Blocks.KELP)) {
                        level.setBlock(floorState, kelpState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        i++;
                    }
                    break;
                }

                current = current.above();
            }
        }

        return i > 0;
    }
}