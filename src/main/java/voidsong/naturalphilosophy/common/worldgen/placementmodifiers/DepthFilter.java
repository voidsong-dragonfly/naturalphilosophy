package voidsong.naturalphilosophy.common.worldgen.placementmodifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import voidsong.naturalphilosophy.common.worldgen.NPPlacementModifiers;

import javax.annotation.Nonnull;

public class DepthFilter extends PlacementFilter {
    public static final MapCodec<DepthFilter> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.optionalFieldOf("min_inclusive", Integer.MIN_VALUE).forGetter(r -> r.minInclusive),
                Codec.INT.optionalFieldOf("max_inclusive", Integer.MAX_VALUE).forGetter(r -> r.maxInclusive)
            )
            .apply(builder, DepthFilter::new)
    );
    private final int minInclusive;
    private final int maxInclusive;

    private DepthFilter(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static DepthFilter of(int minInclusive, int maxInclusive) {
        return new DepthFilter(minInclusive, maxInclusive);
    }

    @Override
    protected boolean shouldPlace(@Nonnull PlacementContext context, @Nonnull RandomSource random, BlockPos pos) {
        int height = pos.getY();
        return height >= minInclusive && random.nextInt(maxInclusive - minInclusive) > (maxInclusive - height);
    }

    @Override
    @Nonnull
    public PlacementModifierType<?> type() {
        return NPPlacementModifiers.DEPTH_FILTER.get();
    }
}
