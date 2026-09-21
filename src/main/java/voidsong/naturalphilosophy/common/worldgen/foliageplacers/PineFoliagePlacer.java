package voidsong.naturalphilosophy.common.worldgen.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import voidsong.naturalphilosophy.common.worldgen.NPFoliagePlacers;

import javax.annotation.Nonnull;

public class PineFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(IntProvider.codec(0, 24).fieldOf("crown_height").forGetter(placer -> placer.crownHeight))
                    .apply(instance, PineFoliagePlacer::new)
    );
    public final IntProvider crownHeight;

    public PineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return NPFoliagePlacers.PINE.get();
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
        BlockPos blockpos = attachment.pos();
        int prevRadius = 0;

        for (int yPos = blockpos.getY() - foliageHeight + offset; yPos <= blockpos.getY() + offset; yPos++) {
            int downwardsOffset = blockpos.getY() - yPos;
            int radius = foliageRadius + attachment.radiusOffset() + Mth.floor((float)downwardsOffset / (float)foliageHeight * 2.5F);
            int range;
            if (radius == prevRadius && downwardsOffset == 1 || (downwardsOffset > 2 && (yPos & 1) == 0)) {
                range = radius + 1;
            } else {
                range = radius;
            }

            this.placeLeavesRow(level, blockSetter, random, config, new BlockPos(blockpos.getX(), yPos, blockpos.getZ()), range, 0, attachment.doubleTrunk());
            prevRadius = radius;
        }
    }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return this.crownHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return localX + localZ >= 7 || localX * localX + localZ * localZ > range * range;
    }
}
