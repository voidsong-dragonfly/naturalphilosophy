package voidsong.naturalphilosophy.common.worldgen.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import voidsong.naturalphilosophy.common.worldgen.NPFoliagePlacers;

import javax.annotation.Nonnull;

public class RoundedBlobFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<RoundedBlobFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height))
                    .apply(instance, RoundedBlobFoliagePlacer::new)
    );
    protected final int height;

    public RoundedBlobFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return NPFoliagePlacers.ROUNDED_BLOB.get();
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
        for (int i = offset; i >= offset - foliageHeight; i--) {
            int j = Math.max(foliageRadius + attachment.radiusOffset() - 1 - i / 2, 0);
            this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), j, i, attachment.doubleTrunk());
        }
        int depth = -1;
        for (int k = Math.max(foliageRadius + attachment.radiusOffset() - 2 - (offset - foliageHeight) / 2, 0); k > 0; k--) {
            this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), k, offset - foliageHeight + depth, attachment.doubleTrunk());
            depth--;
        }
    }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return this.height;
    }

    /**
     * Skips certain positions based on the provided shape, such as rounding corners randomly.
     * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
     */
    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        boolean notFirstStep = !((localY == -1 && range <= 1) || (localY == 0 && (random.nextInt(3) == 0)));
        boolean corners = localX == range && localZ == range || (range >= 3 && (localX + localZ > (2*range - 2)));
        return notFirstStep && corners;
    }
}

