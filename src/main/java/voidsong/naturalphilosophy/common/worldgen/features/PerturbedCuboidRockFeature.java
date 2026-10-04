package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.*;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.phys.Vec3;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;
import voidsong.naturalphilosophy.common.worldgen.features.PerturbedCuboidRockFeature.PerturbedCuboidRockConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerturbedCuboidRockFeature extends Feature<PerturbedCuboidRockConfiguration> {
    private static final ResourceKey<NormalNoise.NoiseParameters> NOISE = ResourceKey.create(Registries.NOISE, ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, "moss_patch"));
    private static final BlockState MOSS_CARPET = Blocks.MOSS_CARPET.defaultBlockState();
    private static final BlockState SNOW_LAYER = Blocks.SNOW.defaultBlockState();

    public PerturbedCuboidRockFeature(Codec<PerturbedCuboidRockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PerturbedCuboidRockConfiguration> configuration) {
        BlockPos origin = configuration.origin();
        WorldGenLevel level = configuration.level();
        RandomSource random = configuration.random();
        PerturbedCuboidRockConfiguration config = configuration.config();

        // Calculate rotation as individual floats
        float pitch = random.nextFloat()*2f*(float)Math.PI;
        float yaw = random.nextFloat()*2f*(float)Math.PI;
        float roll = random.nextFloat()*2f*(float)Math.PI;

        // Calculate the face centers and their normals
        // Position is calculated first, then rotated. Normals are perturbed, then rotated
        Vec3i size = new Vec3i(config.radius1.sample(random), config.radius2.sample(random), config.radius3.sample(random));
        Vec3 radii = new Vec3(size.getX(), size.getY(), size.getZ());
        Map<Direction, Pair<Vec3, Vec3>> normals = new Object2ObjectArrayMap<>();
        for (Direction direction : Direction.values()) {
            Vec3 normal = new Vec3(direction.getNormal().getX(), direction.getNormal().getY(), direction.getNormal().getZ());
            //normals.put(direction, Pair.of(radii.multiply(normal), perturbNormal(normal, random)));
            normals.put(direction, Pair.of(radii.multiply(normal).xRot(pitch).yRot(yaw).zRot(roll), perturbNormal(normal, random).xRot(pitch).yRot(yaw).zRot(roll)));
        }

        // Prep before running the check pass
        int originSurfaceHeight = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        int maxRadius = (int)Math.ceil(Math.max(Math.max(size.getX(), size.getY()), size.getZ())*1.5);
        List<BlockPos> exteriorStonePositions = new ArrayList<>();
        List<BlockPos> stonePositions = new ArrayList<>();
        List<BlockPos> aggregatePositions = new ArrayList<>();
        List<BlockPos> soilPositions = new ArrayList<>();
        MutableBlockPos pos = new MutableBlockPos();
        // Check which positions are available to be placed in. The rock will not be placed if an area above the ground
        // intersects blocks that are "solid terrain", such as all types of stone
        for (int x = -maxRadius; x<=maxRadius; x++)
            for (int y = -maxRadius; y<=maxRadius; y++)
                for (int z = -maxRadius; z<=maxRadius; z++) {
                    double maxDistance = -Double.MAX_VALUE;
                    // Check the maximum distance to the exterior convex hull. For all blocks whose center is inside the hull, this should be <0
                    for (Pair<Vec3, Vec3> faceData : normals.values()) {
                        Vec3 toFaceCenter = new Vec3(x - faceData.getFirst().x, y - faceData.getFirst().y, z - faceData.getFirst().z);
                        maxDistance = Math.max(toFaceCenter.dot(faceData.getSecond()), maxDistance);
                    }
                    // Add the block to the place list if maximum distance is smaller than the criterion distance (0)
                    if (maxDistance < 0) {
                        pos.setWithOffset(origin, x, y, z);
                        // Stone from the rock, & gravel if it collides with the stone below
                        if (level.getBlockState(pos).is(config.intersectionCancelsPlacement)) {
                            if ((origin.getY() + y) > originSurfaceHeight) return false;
                            else if (!level.getBlockState(pos.above()).is(config.stoneAmendmentReplaceable)) aggregatePositions.add(pos.immutable());
                        } else {
                            // Add any blocks of the -1 isosurface to the list to check if they border an air block
                            if (maxDistance > -1) exteriorStonePositions.add(pos.immutable());
                            stonePositions.add(pos.immutable());
                        }
                        // Soil adjustments to prevent grass beneath rocks & to differentiate the feature from the rock beneath it. Both use isosurface -1 to limit checked positions
                        if (maxDistance > -1 && level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos).getY() == pos.getY()) {
                            BlockState dirt = level.getBlockState(pos.below());
                            if (dirt.is(BlockTags.DIRT)) soilPositions.add(pos.below().immutable());
                        } else if (maxDistance > -1 && !level.getBlockState(pos).is(config.stoneAmendmentReplaceable)) {
                            BlockState stone = level.getBlockState(pos.below());
                            if (stone.is(config.stoneAmendmentReplaceable)) aggregatePositions.add(pos.below().immutable());
                        }
                    }
                }

        //Place blocks at the available positions if we did not fail the checks
        // We place stone, then once the rock's shape is established we modify it to ensure we placed accurately.
        for (BlockPos stone : stonePositions)
            level.setBlock(stone, config.rock.getState(random, stone), 2);
        // First comes the exterior replacement & decoration layer placement
        for (BlockPos stone : exteriorStonePositions) {
            boolean exposed = false;
            // Check if the potentially exterior block is exposed
            for (Direction direction : Direction.values())
                if (level.getBlockState(stone.relative(direction)).canBeReplaced()) {
                    level.setBlock(stone, config.exteriorRock.getState(random, stone), 2);
                    exposed = true;
                    break;
                }
            // If the block is exposed we operate on the decorations such as snow and moss layer placement, & amend plants that should be removed
            if (exposed) {
                BlockPos above = stone.above();
                BlockState relative = level.getBlockState(above);
                // Snow and moss layer placement, snow layer placement has priority
                if (relative.isAir()) {
                    Biome biome = level.getBiome(above).value();
                    if (config.snowy() && biome.shouldSnow(level, above)) {
                        level.setBlock(above, SNOW_LAYER, 2);
                        BlockPos oldSurface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, above);
                        if (biome.shouldSnow(level, oldSurface)) {
                            level.setBlock(oldSurface, SNOW_LAYER, 2);
                            BlockState grass = level.getBlockState(oldSurface.below());
                            if (grass.hasProperty(SnowyDirtBlock.SNOWY)) {
                                level.setBlock(oldSurface.below(), grass.setValue(SnowyDirtBlock.SNOWY, true), 2);
                            }
                        }
                    } else if (random.nextDouble() <= config.mossChance() && level.getLevel().getChunkSource().randomState().getOrCreateNoise(NOISE).getValue(stone.getX(), 0, stone.getZ()) > 0)
                        level.setBlock(above, MOSS_CARPET, 2);
                }
                // Amend plants placed above the rock
                if (!relative.canSurvive(level, above)) {
                    if (relative.getBlock() instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock())
                        level.setBlock(above, extension.naturalphilosophy$getRootsBlock().defaultBlockState(), 3);
                    else {
                        level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                        if (!level.getBlockState(stone.above(2)).canSurvive(level, stone.above(2)))
                            level.setBlock(stone.above(2), Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        // Then we place the ground-down aggregate bed and amend any grass/podzol/etc
        for (BlockPos aggregate : aggregatePositions)
            level.setBlock(aggregate, config.aggregate.getState(random, aggregate), 2);
        for (BlockPos soil : soilPositions)
            if(level.getBlockState(soil).is(config.soilAmendmentReplaceable)) level.setBlock(soil, config.soil.getState(random, soil), 2);

        return true;
    }

    private Vec3 perturbNormal(Vec3 normal, RandomSource random) {
        return normal.add(0.6*(random.nextDouble()-0.5d), 0.6*(random.nextDouble()-0.5d), 0.6*(random.nextDouble()-0.5d)).normalize();
    }

    public record PerturbedCuboidRockConfiguration(BlockStateProvider rock, BlockStateProvider exteriorRock, BlockStateProvider aggregate, BlockStateProvider soil,
                                                   HolderSet<Block> intersectionCancelsPlacement, HolderSet<Block> stoneAmendmentReplaceable, HolderSet<Block> soilAmendmentReplaceable,
                                                   double mossChance, boolean snowy, IntProvider radius1, IntProvider radius2, IntProvider radius3) implements FeatureConfiguration {
        public PerturbedCuboidRockConfiguration(BlockStateProvider rock, BlockStateProvider aggregate, BlockStateProvider soil,
                                                HolderSet<Block> intersectionCancelsPlacement, HolderSet<Block> stoneAmendmentReplaceable, HolderSet<Block> soilAmendmentReplaceable,
                                                double mossChance, boolean snowy, IntProvider radius) {
            this(rock, rock, aggregate, soil, intersectionCancelsPlacement, stoneAmendmentReplaceable, soilAmendmentReplaceable, mossChance, snowy, radius, radius, radius);
        }

        public static final Codec<PerturbedCuboidRockConfiguration> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(builder -> builder.group(
                BlockStateProvider.CODEC.fieldOf("rock").forGetter(PerturbedCuboidRockConfiguration::rock),
                BlockStateProvider.CODEC.fieldOf("exterior_rock").forGetter(PerturbedCuboidRockConfiguration::exteriorRock),
                BlockStateProvider.CODEC.fieldOf("aggregate").forGetter(PerturbedCuboidRockConfiguration::aggregate),
                BlockStateProvider.CODEC.fieldOf("soil").forGetter(PerturbedCuboidRockConfiguration::soil),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("intersection_cancels_placement").forGetter(PerturbedCuboidRockConfiguration::intersectionCancelsPlacement),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("stone_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::stoneAmendmentReplaceable),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("soil_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::soilAmendmentReplaceable),
                Codec.DOUBLE.optionalFieldOf("moss_chance", 0.0).forGetter(PerturbedCuboidRockConfiguration::mossChance),
                Codec.BOOL.optionalFieldOf("snowy", false).forGetter(PerturbedCuboidRockConfiguration::snowy),
                IntProvider.CODEC.fieldOf("radius_1").forGetter(PerturbedCuboidRockConfiguration::radius1),
                IntProvider.CODEC.fieldOf("radius_2").forGetter(PerturbedCuboidRockConfiguration::radius2),
                IntProvider.CODEC.fieldOf("radius_3").forGetter(PerturbedCuboidRockConfiguration::radius3)
            ).apply(builder, PerturbedCuboidRockConfiguration::new)),
            RecordCodecBuilder.create(builder -> builder.group(
                BlockStateProvider.CODEC.fieldOf("rock").forGetter(PerturbedCuboidRockConfiguration::rock),
                BlockStateProvider.CODEC.fieldOf("aggregate").forGetter(PerturbedCuboidRockConfiguration::aggregate),
                BlockStateProvider.CODEC.fieldOf("soil").forGetter(PerturbedCuboidRockConfiguration::soil),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("intersection_cancels_placement").forGetter(PerturbedCuboidRockConfiguration::intersectionCancelsPlacement),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("stone_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::stoneAmendmentReplaceable),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("soil_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::soilAmendmentReplaceable),
                Codec.DOUBLE.optionalFieldOf("moss_chance", 0.0).forGetter(PerturbedCuboidRockConfiguration::mossChance),
                Codec.BOOL.optionalFieldOf("snowy", false).forGetter(PerturbedCuboidRockConfiguration::snowy),
                IntProvider.CODEC.fieldOf("radius").forGetter(PerturbedCuboidRockConfiguration::radius1)
            ).apply(builder, PerturbedCuboidRockConfiguration::new)));
    }
}