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
                Codec.INT.optionalFieldOf("taper_end", Integer.MIN_VALUE).forGetter(r -> r.taperEnd),
                Codec.INT.optionalFieldOf("taper_start", Integer.MAX_VALUE).forGetter(r -> r.taperStart)
            )
            .apply(builder, DepthFilter::new)
    );
    private final int taperEnd;
    private final int taperStart;

    private DepthFilter(int taperEnd, int taperStart) {
        this.taperEnd = taperEnd;
        this.taperStart = taperStart;
    }

    public static DepthFilter of(int taperEnd, int taperStart) {
        return new DepthFilter(taperEnd, taperStart);
    }

    @Override
    protected boolean shouldPlace(@Nonnull PlacementContext context, @Nonnull RandomSource random, BlockPos pos) {
        int height = pos.getY();
        boolean topDown = height >= taperEnd && random.nextInt(Math.abs(taperStart - taperEnd)) > (taperStart - height);
        boolean bottomUp = height <= taperEnd && random.nextInt(Math.abs(taperEnd - taperStart)) > (height-taperStart);
        return taperStart < taperEnd ? bottomUp : topDown;
    }

    @Override
    @Nonnull
    public PlacementModifierType<?> type() {
        return NPPlacementModifiers.DEPTH_FILTER.get();
    }
}
