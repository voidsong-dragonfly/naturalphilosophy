package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;
import voidsong.naturalphilosophy.common.worldgen.features.CliffsFeature.CliffsConfiguration;

public class CliffsFeature extends Feature<CliffsConfiguration> {
    public CliffsFeature(Codec<CliffsConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CliffsConfiguration> context) {
        CliffsConfiguration configuration = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Data storage for cliff & cliff-edge processing
        boolean[][] cliffPlacement = new boolean[18][18];
        boolean[][] caveLipEdge = new boolean[18][18];

        // Fill the placement arrays with their values so they can be processed
        MutableBlockPos mut = origin.mutable();
        for (int i = -1; i < 17; i++) {
            for (int j = -1; j < 17; j++) {
                // Get heightmap heights for the five blocks in a + pattern we care about for slope
                int centerHeight = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i, 0, j)).getY();
                int northHeight = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i, 0, j - 1)).getY();
                int eastHeight  = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i + 1, 0, j)).getY();
                int southHeight = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i, 0, j + 1)).getY();
                int westHeight  = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i - 1, 0, j)).getY();
                // Get actual block position of this coordinate
                int blockY = centerHeight - 1;
                // Get adjacent heightmap min & max heights. We include this block in max but not min to keep spires but not pits as stone
                int min = Math.min(northHeight, Math.min(eastHeight, Math.min(southHeight, westHeight)));
                int maxAdjacent = Math.max(northHeight, Math.max(eastHeight, Math.max(southHeight, westHeight)));
                int max = Math.max(centerHeight, maxAdjacent);
                // Use the height difference we need to check to ensure this is a cliff (max - min >= 3) or a one-block spire (max > maxAdjacent)
                if ((max - min) < 3 && !(max > maxAdjacent && (max - min) == 2)) continue;
                // Check if we're on a cliff lip (low depth, flat height profile) based on the top of this block column if run at the surface
                mut.setWithOffset(origin, i, 0, j).setY(blockY);
                boolean lip = false;
                for (int k = 0; k < 3; k++) {
                    mut.move(Direction.DOWN);
                    if (level.getBlockState(mut).canBeReplaced()) {
                        lip = true;
                        break;
                    }
                }
                if (lip && (max - blockY < 3)) continue;
                // Skip the next check if we're at in the middle of a large cliff, regardless of depth,
                // to prevent 3D cliffs from having grass streaks due to lip-like behavior
                if (!((max - blockY) > 11 && (blockY - min) > 1)) {
                    // Set offset height
                    mut.setWithOffset(origin, i, 0, j).setY(blockY + 2);
                    boolean caveLipFloor = false;
                    // Check that we're not at the bottom of a hanging-over cave entrance by checking if there's air pockets beneath ground height on the sides
                    for (Direction offset : Direction.Plane.HORIZONTAL) {
                        // Get the replaceability of the block to any side, then reset the mutable pos
                        mut.move(offset);
                        boolean replaceable = level.getBlockState(mut).canBeReplaced();
                        // We need to always analyze the bottom block to ensure we don't desync from the true position
                        replaceable = level.getBlockState(mut.move(Direction.DOWN)).canBeReplaced() || replaceable;
                        mut.move(offset.getOpposite()).move(Direction.UP);
                        // Check the height to see if there is a replaceable block below heightmap
                        int surfaceHeight = switch (offset) {
                            case NORTH -> northHeight;
                            case EAST -> eastHeight;
                            case SOUTH -> southHeight;
                            case WEST -> westHeight;
                            default -> Integer.MIN_VALUE;
                        };
                        if (replaceable && blockY + 2 < surfaceHeight) {
                            caveLipFloor = true;
                        }
                    }
                    // Update the cave lip edge array and continue
                    if (caveLipFloor) {
                        if (i >= 0 && i <= 15 && j >= 0 && j <= 15) caveLipEdge[i + 1][j + 1] = true;
                        continue;
                    }
                }
                // Update the cliff array for later processing
                cliffPlacement[i + 1][j + 1] = true;
            }
        }

        // Process the placement arrays
        for (int i = 1; i < 17; i++) {
            for (int j = 1; j < 17; j++) {
                // Remove any cliff areas that are single columns with no cliffs surrounding them
                boolean surroundings = false;
                for (Direction offset : Direction.Plane.HORIZONTAL) {
                    surroundings = cliffPlacement[i + offset.getStepX()][j + offset.getStepZ()];
                    if (surroundings) break;
                }
                if (!surroundings) cliffPlacement[i][j] = false;
                // Touch up cliff areas where the center point of an area between cliff sections is not but should be
            }
        }

        // Actually place the subset of the placement arrays that fits within the chunk
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (cliffPlacement[i+1][j+1]) {
                    int centerHeight = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, mut.setWithOffset(origin, i, 0, j)).getY();
                    // Place the cliff block if we don't need to passthrough it. Default passthrough is for all ores, stones, terracotta, etc.
                    mut.setWithOffset(origin, i, 0, j ).setY(centerHeight);
                    boolean place;
                    // We only iterate up to a max of 16 blocks down as blocks that need amendment should not place below that
                    while ((place = configuration.replaceable.test(level, mut.move(Direction.DOWN))) || configuration.passthrough.test(level, mut) && (centerHeight - mut.getY() < 16)) {
                        if (place) level.setBlock(mut, configuration.provider.getState(context.random(), mut), 2);
                    }
                    // Remove any grass or other plants that can't survive above the cliff; amend kelp
                    mut.setWithOffset(origin, i, 0, j ).setY(centerHeight);
                    BlockState relative = level.getBlockState(mut);
                    if (!relative.canSurvive(level, mut)) {
                        if (relative.getBlock() instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock())
                            level.setBlock(mut, extension.naturalphilosophy$getRootsBlock().defaultBlockState(), 3);
                        else {
                            BlockState replace = relative.hasProperty(BlockStateProperties.WATERLOGGED) && relative.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                            level.setBlock(mut, replace, 3);
                            if (!level.getBlockState(mut.move(Direction.UP)).canSurvive(level, mut))
                                level.setBlock(mut, replace, 3);
                        }
                    }
                }
            }
        }

        return true;
    }

    public record CliffsConfiguration(BlockStateProvider provider, BlockPredicate replaceable, BlockPredicate passthrough) implements FeatureConfiguration {
        public static final Codec<CliffsConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("to_place").forGetter(CliffsConfiguration::provider),
            BlockPredicate.CODEC.fieldOf("replaceable").forGetter(CliffsConfiguration::replaceable),
            BlockPredicate.CODEC.fieldOf("passthrough").forGetter(CliffsConfiguration::passthrough)
        ).apply(builder, CliffsConfiguration::new));
    }
}
