package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
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
        CliffsConfiguration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Pre-process to mark one to two block thick overhangs without any other overhangs within a 3x3 area centered on them
        // These mess up detection by being processed as overhangs to be avoided placement for & overhangs to not place by
        byte[][] overhangDepth = new byte[18][18];
        MutableBlockPos mut = origin.mutable();
        for (int i = -1; i < 17; i++)
            for (int j = -1; j < 17; j++) {
                int centerHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j));
                mut.setWithOffset(origin, i, 0, j).setY(centerHeight);
                for (byte k = 0; k < 6; k++) {
                    mut.move(Direction.DOWN);
                    if (level.getBlockState(mut).canBeReplaced()) {
                        overhangDepth[i + 1][j + 1] = k;
                        break;
                    }
                }
            }
        // Remove all the marked overhangs by replacing them with air blocks
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
                if (overhangDepth[i+1][j+1] < 3 && overhangDepth[i+1][j+1] > 0) {
                    boolean neighbors = false;
                    for (int k = -1; k < 2 && !neighbors; k++)
                        for (int l = -1; l < 2; l++) {
                            if (overhangDepth[i + 1 + k][j + 1 + l] != 0 && !(k == 0 && l == 0)) {
                                neighbors = true;
                                break;
                            }
                        }
                    if (!neighbors) {
                        int centerHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j));
                        level.setBlock(mut.setWithOffset(origin, i, 0, j).setY(centerHeight - 1), Blocks.AIR.defaultBlockState(), 2);
                        if (overhangDepth[i+1][j+1] == 2) level.setBlock(mut.below(), Blocks.AIR.defaultBlockState(), 2);
                    }
                }

        // Data storage for cliff & cliff-edge processing
        boolean[][] cliffPlacement = new boolean[18][18];
        boolean[][] cliffAmendmentPlacement = new boolean[18][18];
        boolean[][] caveLipEdge = new boolean[18][18];

        // Fill the placement arrays with their values so they can be processed
        for (int i = -1; i < 17; i++) {
            for (int j = -1; j < 17; j++) {
                // Get heightmap heights for the five blocks in a + pattern we care about for slope
                int centerHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j));
                int northHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j - 1));
                int eastHeight  = getHeight(level, mut.setWithOffset(origin, i + 1, 0, j));
                int southHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j + 1));
                int westHeight  = getHeight(level, mut.setWithOffset(origin, i - 1, 0, j));
                // Get actual block position of this coordinate
                int blockY = centerHeight - 1;
                // Get adjacent heightmap min & max heights. We include this block in max but not min to keep spires but not pits as stone
                int min = Math.min(northHeight, Math.min(eastHeight, Math.min(southHeight, westHeight)));
                int maxAdjacent = Math.max(northHeight, Math.max(eastHeight, Math.max(southHeight, westHeight)));
                int max = Math.max(centerHeight, maxAdjacent);
                // Check if we're on a cliff lip (low depth, flat height profile) based on the top of this block column if run at the surface
                if (overhangDepth[i+1][j+1] > 0 && overhangDepth[i+1][j+1] < 4 && (max - blockY < 3)) continue;
                // Add to the potential amendment placement area if we are just on the border
                if ((max - min) == 2 && !(max > maxAdjacent)) cliffAmendmentPlacement[i + 1][j + 1] = true;
                // Use the height difference we need to check to ensure this is a cliff (max - min >= 3) or a one-block spire (max > maxAdjacent)
                if ((max - min) < 3 && !(max > maxAdjacent && (max - min) == 2)) continue;
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
                // Check if we're at the bottom of the edge of a shallow cave (stone bottom, smaller than 5 block drop)
                // We filter this to within the current chunk to not filter out already-placed cliffs from other chunks
                if ((i >= 0 && i <= 15 && j >= 0 && j <= 15) && (maxAdjacent - centerHeight) < 5) {
                    if (config.passthrough.test(level, mut.setWithOffset(origin, i, 0, j).setY(blockY))) continue;
                }
                // Update the cliff array for later processing
                cliffPlacement[i + 1][j + 1] = true;
            }
        }

        // Process the placement arrays
        // We iterate once per check rather than once overall to ensure one pass is finished before the next
        // Remove any cliff areas that are single columns with no cliffs surrounding them
        for (int i = 1; i < 17; i++)
            for (int j = 1; j < 17; j++) {
                if (cliffPlacement[i][j]) {
                    boolean surroundings = false;
                    for (Direction offset : Direction.Plane.HORIZONTAL) {
                        surroundings = surroundings || cliffPlacement[i + offset.getStepX()][j + offset.getStepZ()];
                    }
                    if (!surroundings) cliffPlacement[i][j] = false;
                }
            }
        // Fill in any potential cliff areas that are two blocks tall and surrounded by three or more cliff blocks
        for (int i = 1; i < 17; i++)
            for (int j = 1; j < 17; j++)
                if (cliffAmendmentPlacement[i][j]) {
                    int surroundings = 0;
                    for (Direction offset : Direction.Plane.HORIZONTAL) {
                        surroundings += cliffPlacement[i + offset.getStepX()][j + offset.getStepZ()] ? 1 : 0;
                    }
                    if (surroundings > 2) cliffPlacement[i][j] = true;
                }
        // Remove any cliff areas that are double columns with no cliffs surrounding them
        for (int i = 1; i < 17; i++)
            for (int j = 1; j < 17; j++) {
                if (cliffPlacement[i][j]) {
                    int surroundings = 0;
                    Direction second = null;
                    for (Direction offset : Direction.Plane.HORIZONTAL) {
                        if (cliffPlacement[i + offset.getStepX()][j + offset.getStepZ()]) {
                            surroundings++;
                            boolean surroundingsRelative = false;
                            for (Direction offsetRelative : Direction.Plane.HORIZONTAL) {
                                surroundingsRelative = surroundingsRelative || (!offsetRelative.equals(offset.getOpposite()) && arrayGetOrDefault(cliffPlacement, i, j, offset, offsetRelative, true));
                            }
                            if (!surroundingsRelative) second = offset;
                        }
                    }
                    if (surroundings == 1 && second != null) {
                        cliffPlacement[i][j] = false;
                        cliffPlacement[i + second.getStepX()][j + second.getStepZ()] = false;
                    }
                }
            }


        // Actually place the subset of the placement arrays that fits within the chunk
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (cliffPlacement[i+1][j+1]) {
                    int centerHeight = getHeight(level, mut.setWithOffset(origin, i, 0, j));
                    // Place the cliff block if we don't need to passthrough it. Default passthrough is for all ores, stones, terracotta, etc.
                    mut.setWithOffset(origin, i, 0, j ).setY(centerHeight);
                    boolean place;
                    // We only iterate up to a max of 16 blocks down as blocks that need amendment should not place below that
                    while ((place = config.replaceable.test(level, mut.move(Direction.DOWN))) || config.passthrough.test(level, mut) && (centerHeight - mut.getY() < 16)) {
                        if (place) level.setBlock(mut, config.provider.getState(context.random(), mut), 2);
                    }
                    // Remove any grass or other plants that can't survive above the cliff; amend kelp
                    mut.setWithOffset(origin, i, 0, j ).setY(centerHeight);
                    BlockState relative = level.getBlockState(mut);
                    if (!relative.canSurvive(level, mut)) {
                        if (relative.getBlock() instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock())
                            level.setBlock(mut, extension.naturalphilosophy$getRootsBlock().defaultBlockState(), 3);
                        else {
                            level.setBlock(mut, relative.hasProperty(BlockStateProperties.WATERLOGGED) && relative.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                            relative = level.getBlockState(mut.move(Direction.UP));
                            if (!relative.canSurvive(level, mut))
                                level.setBlock(mut, relative.hasProperty(BlockStateProperties.WATERLOGGED) && relative.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Gets the "true" height of the top of terrain for a column
     * Takes the lowest of OCEAN_FLOOR and OCEAN_FLOOR_WG to allow for filtering to work as well as get underneath trees
     */
    private static int getHeight(LevelReader level, BlockPos pos) {
        return Math.min(level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, pos).getY(), level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, pos).getY());
    }

    /**
     * Gets placement from the provided (18x18) array, or returns true if out of range
     */
    private static boolean arrayGetOrDefault(boolean[][] placement, int i, int j, Direction offset1, Direction offset2, boolean defaultValue) {
        int indexX = i + offset1.getStepX() + offset2.getStepX();
        int indexZ = j + offset1.getStepZ() + offset2.getStepZ();
        if (indexX < 0 || indexX > 17 || indexZ < 0 || indexZ > 17) {
            return defaultValue;
        } else {
            return placement[indexX][indexZ];
        }
    }

    public record CliffsConfiguration(BlockStateProvider provider, BlockPredicate replaceable, BlockPredicate passthrough, boolean test) implements FeatureConfiguration {
        public static final Codec<CliffsConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("to_place").forGetter(CliffsConfiguration::provider),
            BlockPredicate.CODEC.fieldOf("replaceable").forGetter(CliffsConfiguration::replaceable),
            BlockPredicate.CODEC.fieldOf("passthrough").forGetter(CliffsConfiguration::passthrough),
            Codec.BOOL.optionalFieldOf("test", false).forGetter(CliffsConfiguration::test)
        ).apply(builder, CliffsConfiguration::new));
    }
}
