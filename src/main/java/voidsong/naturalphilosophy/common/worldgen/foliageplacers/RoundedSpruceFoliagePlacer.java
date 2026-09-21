package voidsong.naturalphilosophy.common.worldgen.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import voidsong.naturalphilosophy.common.worldgen.NPFoliagePlacers;

import javax.annotation.Nonnull;

public class RoundedSpruceFoliagePlacer extends SpruceFoliagePlacer {
    public static final MapCodec<RoundedSpruceFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
                    .apply(instance, RoundedSpruceFoliagePlacer::new)
    );

    public RoundedSpruceFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset, trunkHeight);
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return NPFoliagePlacers.ROUNDED_SPRUCE.get();
    }

    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        if (range < 3 ) {
            return localX == range && localZ == range && range > 0 && (range > 1 || localY > -5);
        } else if (range == 3) {
            return ((localX >= range -1) && (localZ >= range-1)) && !(localX == localZ && localX == (range -1));
        } else if (range == 4) {
            return (localX >= range -2 && localZ == range) || (localZ >= range -2 && localX == range);
        } else {
            return localX*localX + localZ+localZ <= range*range;
        }
    }
}
