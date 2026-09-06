package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.worldgen.features.CustomizableKelpFeature.KelpConfiguration;

public class CustomizableKelpFeature extends Feature<KelpConfiguration> {
    public CustomizableKelpFeature(Codec<KelpConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<KelpConfiguration> config) {
        int i = 0;
        WorldGenLevel level = config.level();
        BlockPos pos = config.origin();
        RandomSource random = config.random();
        // Add an early exit if we are less than five blocks below water for the default surface heightmap pre-ice
        if (level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()) + 5 > level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()))
            return false;
        // Continue on with the rest of the
        int j = level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ());
        BlockPos current = j > level.getSeaLevel() ? pos : new BlockPos(pos.getX(), j, pos.getZ());
        if (level.getBlockState(current.below()).is(BlockTags.ICE) && j > pos.getY())
            current = pos;
        if (level.getBlockState(current).is(Blocks.WATER)) {
            BlockState kelpState = config.config().tip().getState(random, current);
            BlockState kelpPlantState = config.config().plant().getState(random, current);
            BlockState kelpRootsState = config.config().roots().getState(random, current);
            int k = 1 + random.nextInt(10);

            for (int l = 0; l <= k; l++) {
                if (level.getBlockState(current).is(Blocks.WATER) && level.getBlockState(current.above()).is(Blocks.WATER) && (l == 0 ? kelpRootsState : kelpPlantState).canSurvive(level, current)) {
                    if (l == k) {
                        level.setBlock(current, kelpState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        i++;
                    } else {
                        level.setBlock(current, l == 0 ? kelpRootsState : kelpPlantState, 2);
                    }
                } else if (l > 0) {
                    BlockPos floorPos = current.below();
                    if (kelpState.canSurvive(level, floorPos) && !level.getBlockState(floorPos.below()).is(Blocks.KELP)) {
                        level.setBlock(floorPos, kelpState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        i++;
                    }
                    break;
                }

                current = current.above();
            }
        }

        return i > 0;
    }

    public record KelpConfiguration(BlockStateProvider tip, BlockStateProvider plant, BlockStateProvider roots) implements FeatureConfiguration {
        public static final Codec<KelpConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                BlockStateProvider.CODEC.optionalFieldOf("tip", BlockStateProvider.simple(Blocks.KELP)).forGetter(KelpConfiguration::tip),
                BlockStateProvider.CODEC.optionalFieldOf("plant", BlockStateProvider.simple(Blocks.KELP_PLANT)).forGetter(KelpConfiguration::plant),
                BlockStateProvider.CODEC.optionalFieldOf("roots", BlockStateProvider.simple(NPBlocks.KELP_ROOTS.get())).forGetter(KelpConfiguration::roots)
        ).apply(builder, KelpConfiguration::new));
    }
}