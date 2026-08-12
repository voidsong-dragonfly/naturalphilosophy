package voidsong.naturalphilosophy.common.worldgen.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import voidsong.naturalphilosophy.common.worldgen.NPFoliagePlacers;

import javax.annotation.Nonnull;

public class RedMushroomCapFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<RedMushroomCapFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance).apply(instance, RedMushroomCapFoliagePlacer::new)
    );

    public RedMushroomCapFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return NPFoliagePlacers.RED_MUSHROOM_CAP.get();
    }

    @Override
    protected void createFoliage(
            @Nonnull LevelSimulatedReader level,
            @Nonnull FoliagePlacer.FoliageSetter blockSetter,
            @Nonnull RandomSource random,
            @Nonnull TreeConfiguration config,
            int maxFreeTreeHeight,
            @Nonnull FoliagePlacer.FoliageAttachment attachment,
            int foliageHeight,
            int foliageRadius,
            int offset
    ) {
        for (int i = offset; i > offset - 2*foliageRadius; i--) {
            // Place leaves
            this.placeLeavesRow(level, blockSetter, random, config, attachment.pos().above(offset), (i == offset) ? (foliageRadius - 1) : foliageRadius, i-offset, attachment.doubleTrunk());
            // Place offset stem
            BlockPos stem = attachment.pos().above(i);
            if (i != offset) blockSetter.set(stem, config.trunkProvider.getState(random, stem).trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y));
        }
        // Place one last final block for the stem
        BlockPos stem = attachment.pos().above(offset - 2*foliageRadius);
        blockSetter.set(stem, config.trunkProvider.getState(random, stem).trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y));
    }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        boolean capExterior =  localY == 0 && (localZ > range || localX > range);
        boolean edgeOrInside = localY < 0 && (((localX == localZ) && (localX == range)) || (localX < range && localZ < range));
        return capExterior || edgeOrInside;
    }

    @Override
    protected void placeLeavesRow(
        @Nonnull LevelSimulatedReader level,
        @Nonnull FoliagePlacer.FoliageSetter foliageSetter,
        @Nonnull RandomSource random,
        @Nonnull TreeConfiguration treeConfiguration,
        @Nonnull BlockPos pos,
        int range,
        int localY,
        boolean large
    ) {
        int i = large ? 1 : 0;
        MutableBlockPos mutable = new MutableBlockPos();

        for (int j = -range; j <= range + i; j++) {
            for (int k = -range; k <= range + i; k++) {
                if (!this.shouldSkipLocationSigned(random, j, localY, k, range, large)) {
                    // Instead of tryPlaceFoliage, we instead roll it custom - no waterlogging, separate concerns
                    mutable.setWithOffset(pos, j, localY, k);
                    BlockState state = treeConfiguration.foliageProvider.getState(random, mutable);
                    // Check to make sure we're placing the inside of the caps correctly
                    if (j == -range) {
                        state = state.trySetValue(HugeMushroomBlock.EAST, false);
                        if (k == (-range+1)) state = state.trySetValue(HugeMushroomBlock.SOUTH, false);
                        else if (k == (range-1)) state = state.trySetValue(HugeMushroomBlock.NORTH, false);
                    } else if (j == range) {
                        state = state.trySetValue(HugeMushroomBlock.WEST, false);
                        if (k == (-range+1)) state = state.trySetValue(HugeMushroomBlock.SOUTH, false);
                        else if (k == (range-1)) state = state.trySetValue(HugeMushroomBlock.NORTH, false);
                    } else if (k == -range) {
                        state = state.trySetValue(HugeMushroomBlock.SOUTH, false);
                        if (j == (-range+1)) state = state.trySetValue(HugeMushroomBlock.EAST, false);
                        else if (j == (range-1)) state = state.trySetValue(HugeMushroomBlock.WEST, false);
                    } else if (k == range) {
                        state = state.trySetValue(HugeMushroomBlock.NORTH, false);
                        if (j == (-range+1)) state = state.trySetValue(HugeMushroomBlock.EAST, false);
                        else if (j == (range-1)) state = state.trySetValue(HugeMushroomBlock.WEST, false);
                    }
                    // Check to make sure we're removing tops correctly
                    if (localY < -1) state = state.trySetValue(HugeMushroomBlock.UP, false);
                    // Actually set the state
                    foliageSetter.set(mutable, state);
                }
            }
        }
    }
}
