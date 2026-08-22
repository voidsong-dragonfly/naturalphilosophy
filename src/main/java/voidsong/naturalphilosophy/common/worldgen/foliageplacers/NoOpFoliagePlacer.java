package voidsong.naturalphilosophy.common.worldgen.foliageplacers;

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

public class NoOpFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<NoOpFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> foliagePlacerParts(instance).apply(instance, NoOpFoliagePlacer::new));

    public NoOpFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return NPFoliagePlacers.NO_OP.get();
    }

    @Override
    protected void createFoliage(
        @Nonnull LevelSimulatedReader level,
        @Nonnull FoliageSetter blockSetter,
        @Nonnull RandomSource random,
        @Nonnull TreeConfiguration config,
        int maxFreeTreeHeight,
        @Nonnull FoliageAttachment attachment,
        int foliageHeight,
        int foliageRadius,
        int offset
    ) { }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return 0;
    }

    /**
     * Skips certain positions based on the provided shape, such as rounding corners randomly.
     * The coordinates are passed in as absolute value, and should be within [0, {@code range}].
     */
    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return true;
    }
}

