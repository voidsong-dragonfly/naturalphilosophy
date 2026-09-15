package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.*;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;
import voidsong.naturalphilosophy.common.worldgen.features.PerturbedCuboidRockFeature.PerturbedCuboidRockConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerturbedCuboidRockFeature extends Feature<PerturbedCuboidRockConfiguration> {
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
        int maxSurfaceHeight = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        int maxRadius = (int)Math.ceil(Math.max(Math.max(size.getX(), size.getY()), size.getZ())*1.5);
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
                            if ((origin.getY() + y) > maxSurfaceHeight) return false;
                            else if (!level.getBlockState(pos.above()).is(config.stoneAmendmentReplaceable)) aggregatePositions.add(pos.immutable());
                        } else {
                            stonePositions.add(pos.immutable());
                        }
                        // Soil adjustments to prevent grass beneath rocks
                        if (level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos).getY() == pos.getY()) {
                            BlockState dirt = level.getBlockState(pos.below());
                            if (dirt.is(BlockTags.DIRT)) soilPositions.add(pos.below().immutable());
                        } else if (level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos).getY() > pos.getY() && !level.getBlockState(pos).is(config.stoneAmendmentReplaceable)) {
                            BlockState stone = level.getBlockState(pos.below());
                            if (stone.is(config.stoneAmendmentReplaceable)) aggregatePositions.add(pos.below().immutable());
                        }
                    }
                }

        //Place blocks at the available positions if we did not fail the checks
        for (BlockPos stone : stonePositions)
            level.setBlock(stone, config.rock.getState(random, stone), 3);
        for (BlockPos aggregate : aggregatePositions)
            level.setBlock(aggregate, config.aggregate.getState(random, aggregate), 3);
        for (BlockPos soil : soilPositions)
            if(level.getBlockState(soil).is(config.soilAmendmentReplaceable)) level.setBlock(soil, config.soil.getState(random, soil), 3);

        return true;
    }

    private Vec3 perturbNormal(Vec3 normal, RandomSource random) {
        return normal.add(0.6*(random.nextDouble()-0.5d), 0.6*(random.nextDouble()-0.5d), 0.6*(random.nextDouble()-0.5d)).normalize();
    }

    public record PerturbedCuboidRockConfiguration(BlockStateProvider rock,
                                                   BlockStateProvider aggregate,
                                                   BlockStateProvider soil,
                                                   HolderSet<Block> intersectionCancelsPlacement,
                                                   HolderSet<Block> stoneAmendmentReplaceable,
                                                   HolderSet<Block> soilAmendmentReplaceable,
                                                   IntProvider radius1,
                                                   IntProvider radius2,
                                                   IntProvider radius3) implements FeatureConfiguration {
        public static final Codec<PerturbedCuboidRockConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("rock").forGetter(PerturbedCuboidRockConfiguration::rock),
            BlockStateProvider.CODEC.fieldOf("aggregate").forGetter(PerturbedCuboidRockConfiguration::aggregate),
            BlockStateProvider.CODEC.fieldOf("soil").forGetter(PerturbedCuboidRockConfiguration::soil),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("intersection_cancels_placement").forGetter(PerturbedCuboidRockConfiguration::intersectionCancelsPlacement),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("stone_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::soilAmendmentReplaceable),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("soil_amendment_replaceable").forGetter(PerturbedCuboidRockConfiguration::soilAmendmentReplaceable),
            IntProvider.CODEC.fieldOf("radius_1").forGetter(PerturbedCuboidRockConfiguration::radius1),
            IntProvider.CODEC.fieldOf("radius_2").forGetter(PerturbedCuboidRockConfiguration::radius2),
            IntProvider.CODEC.fieldOf("radius_3").forGetter(PerturbedCuboidRockConfiguration::radius3)
        ).apply(builder, PerturbedCuboidRockConfiguration::new));
    }
}