package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class NonLimitedRockFeature extends Feature<BlockStateConfiguration> {
    public NonLimitedRockFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> config) {
        BlockPos floor = config.origin();
        WorldGenLevel level = config.level();
        RandomSource random = config.random();

        BlockStateConfiguration stateConfig;
        for (stateConfig = config.config(); floor.getY() > level.getMinBuildHeight() + 3; floor = floor.below()) {
            if (!level.getBlockState(floor.below()).canBeReplaced()) {
                break;
            }
        }

        if (floor.getY() <= level.getMinBuildHeight() + 3) {
            return false;
        } else {
            for (int l = 0; l < 3; l++) {
                int i = random.nextInt(2);
                int j = random.nextInt(2);
                int k = random.nextInt(2);
                float f = (float)(i + j + k) * 0.333F + 0.5F;

                for (BlockPos pos : BlockPos.betweenClosed(floor.offset(-i, -j, -k), floor.offset(i, j, k))) {
                    if (pos.distSqr(floor) <= (double)(f * f)) {
                        level.setBlock(pos, stateConfig.state, 3);
                    }
                }
                floor = floor.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
            }

            return true;
        }
    }
}