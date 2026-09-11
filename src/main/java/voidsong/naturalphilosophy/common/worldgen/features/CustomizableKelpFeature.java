package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
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
import voidsong.naturalphilosophy.common.worldgen.features.CustomizableKelpFeature.KelpConfiguration.KelpStrand;

public class CustomizableKelpFeature extends Feature<KelpConfiguration> {
    public static final KelpStrand KELP = new KelpStrand(BlockStateProvider.simple(Blocks.KELP), BlockStateProvider.simple(Blocks.KELP_PLANT), BlockStateProvider.simple(NPBlocks.KELP_ROOTS.get()));
    public static final KelpStrand BROWN_KELP = new KelpStrand(BlockStateProvider.simple(NPBlocks.BROWN_KELP.get()), BlockStateProvider.simple(NPBlocks.BROWN_KELP_PLANT.get()), BlockStateProvider.simple(NPBlocks.BROWN_KELP_ROOTS.get()));

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
        if (checkGrowthConditions(current, level, config.config())) {
            BlockState kelpState = config.config().kelp.tip().getState(random, current);
            BlockState kelpPlantState = config.config().kelp.plant().getState(random, current);
            BlockState kelpRootsState = config.config().kelp.roots().getState(random, current);
            int k = 1 + random.nextInt(config.config().maximumHeight);

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
                    if (kelpState.canSurvive(level, floorPos) && !level.getBlockState(floorPos.below()).is(kelpState.getBlock())) {
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

    private boolean checkGrowthConditions(BlockPos surface, WorldGenLevel level, KelpConfiguration config) {
        if (!level.getBlockState(surface).is(Blocks.WATER))
            return false;
        if (level.getBlockState(surface.below()).is(config.shipwreckHoldfastAnchors))
            return true;
        if (level.getBlockState(surface.below()).is(config.stoneHoldfastAnchors))
            return config.minimumSedimentDepth == 0;
        for (int i = 0; i<=config.maximumSedimentDepth;) {
            if (level.getBlockState(surface.below(i + 1)).is(config.allowedSedimentCovering)) {
                i++;
            } else return level.getBlockState(surface.below(i + 1)).is(config.stoneHoldfastAnchors) && i>=config.minimumSedimentDepth;
        }
        return false;
    }

    public record KelpConfiguration(KelpStrand kelp,
                                    HolderSet<Block> shipwreckHoldfastAnchors,
                                    HolderSet<Block> stoneHoldfastAnchors,
                                    HolderSet<Block> allowedSedimentCovering,
                                    int minimumSedimentDepth,
                                    int maximumSedimentDepth,
                                    int maximumHeight) implements FeatureConfiguration {
        public record KelpStrand(BlockStateProvider tip, BlockStateProvider plant, BlockStateProvider roots) {
            public static final Codec<KelpStrand> CODEC = Codec.withAlternative(
                RecordCodecBuilder.create(builder -> builder.group(
                    BlockStateProvider.CODEC.fieldOf("tip").forGetter(KelpStrand::tip),
                    BlockStateProvider.CODEC.fieldOf("plant").forGetter(KelpStrand::plant),
                    BlockStateProvider.CODEC.fieldOf("roots").forGetter(KelpStrand::roots)
                ).apply(builder, KelpStrand::new)),
                Codec.STRING.flatXmap(
                    type -> {
                        if (type.equals("minecraft:kelp"))
                            return DataResult.success(KELP);
                        else if (type.equals("naturalphilosophy:brown_kelp"))
                            return DataResult.success(BROWN_KELP);
                        return DataResult.error(() -> (type + " is not a shorthand kelp type."));
                    },
                    type -> {
                        if (type.equals(KELP))
                            return DataResult.success("minecraft:kelp");
                        if (type.equals(BROWN_KELP))
                            return DataResult.success("naturalphilosophy:brown_kelp");
                        return DataResult.error(() -> type + " is not a shorthand kelp type.");
                    }
                ));
        }

        public static final Codec<KelpConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            KelpStrand.CODEC.fieldOf("type").forGetter(KelpConfiguration::kelp),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("shipwreck_holdfast_anchors").forGetter(KelpConfiguration::shipwreckHoldfastAnchors),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("stone_holdfast_anchors").forGetter(KelpConfiguration::stoneHoldfastAnchors),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("holdfast_anchors_through").forGetter(KelpConfiguration::allowedSedimentCovering),
            Codec.INT.fieldOf("minimum_sediment_depth").forGetter(KelpConfiguration::minimumSedimentDepth),
            Codec.INT.fieldOf("maximum_sediment_depth").forGetter(KelpConfiguration::maximumSedimentDepth),
            Codec.INT.optionalFieldOf("maximum_height", 10).forGetter(KelpConfiguration::maximumHeight)
        ).apply(builder, KelpConfiguration::new));
    }

}