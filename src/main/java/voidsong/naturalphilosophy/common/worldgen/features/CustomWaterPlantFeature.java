package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import voidsong.naturalphilosophy.common.worldgen.features.CustomWaterPlantFeature.WaterPlantConfiguration;

public class CustomWaterPlantFeature extends Feature<WaterPlantConfiguration> {
    public CustomWaterPlantFeature(Codec<WaterPlantConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WaterPlantConfiguration> config) {
        boolean success = false;
        WorldGenLevel level = config.level();
        BlockPos pos = config.origin();
        int k = level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ());
        BlockPos floorPos = k > level.getSeaLevel() ? pos : new BlockPos(pos.getX(), k, pos.getZ());
        BlockState state = config.config().provider().getState(config.random(), floorPos);
        if (checkGrowthConditions(floorPos, level, config.config(), state)) {
            if (state.canSurvive(level, floorPos)) {
                if (state.getBlock() instanceof DoublePlantBlock) {
                    BlockPos topPos = floorPos.above();
                    level.setBlock(floorPos, state, 2);
                    level.setBlock(topPos, state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
                } else {
                    level.setBlock(floorPos, state, 2);
                }
                success = true;
            }
        }
        return success;
    }

    private boolean checkGrowthConditions(BlockPos surface, WorldGenLevel level, WaterPlantConfiguration config, BlockState place) {
        if (!level.getBlockState(surface).is(Blocks.WATER))
            return false;
        if ((place.getBlock() instanceof DoublePlantBlock || !config.shallowPlacement) && !level.getBlockState(surface.above()).is(Blocks.WATER))
            return false;
        if (place.getBlock() instanceof DoublePlantBlock && level.getBlockState(surface.above(2)).isAir() && level.getBiome(surface.above()).value().shouldFreeze(level, surface.above()))
            return false;
        else if (level.getBlockState(surface.above()).isAir() && level.getBiome(surface).value().shouldFreeze(level, surface))
            return false;
        if (level.getBlockState(surface.below()).is(config.shipwreckSubstrate))
            return true;
        for (int i = 0; i<=config.maximumSedimentDepth;) {
            if (level.getBlockState(surface.below(i + 1)).is(config.sedimentSubstrate)) {
                i++;
            } else return !(level.isWaterAt(surface.below(i + 1)) && level.getBlockState(surface.below(i + 1)).isAir()) && i>=config.minimumSedimentDepth;
        }
        return false;
    }

    public record WaterPlantConfiguration(BlockStateProvider provider,
                                          HolderSet<Block> shipwreckSubstrate,
                                          HolderSet<Block> sedimentSubstrate,
                                          int minimumSedimentDepth,
                                          int maximumSedimentDepth,
                                          boolean shallowPlacement) implements FeatureConfiguration {
        public static final Codec<WaterPlantConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("to_place").forGetter(WaterPlantConfiguration::provider),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("shipwreck_substrate").forGetter(WaterPlantConfiguration::shipwreckSubstrate),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("sediment_substrate").forGetter(WaterPlantConfiguration::sedimentSubstrate),
            Codec.INT.fieldOf("minimum_sediment_depth").forGetter(WaterPlantConfiguration::minimumSedimentDepth),
            Codec.INT.fieldOf("maximum_sediment_depth").forGetter(WaterPlantConfiguration::maximumSedimentDepth),
            Codec.BOOL.optionalFieldOf("place_in_shallow_water", false).forGetter(WaterPlantConfiguration::shallowPlacement)
        ).apply(builder, WaterPlantConfiguration::new));
    }
}
