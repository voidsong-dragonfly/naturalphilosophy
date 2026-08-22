package voidsong.naturalphilosophy.common.worldgen.placementmodifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import voidsong.naturalphilosophy.common.worldgen.NPPlacementModifiers;

import javax.annotation.Nonnull;

public class CanopyGapFilter extends PlacementFilter {
    public static final MapCodec<CanopyGapFilter> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.fieldOf("radius").forGetter(r -> r.radius),
                Codec.BOOL.optionalFieldOf("square", false).forGetter(r -> r.square),
                Codec.BOOL.optionalFieldOf("invert", false).forGetter(r -> r.invert)
        ).apply(builder, CanopyGapFilter::new)
    );
    private final int radius;
    private final boolean square;
    private final boolean invert;

    private CanopyGapFilter(int radius, boolean square, boolean invert) {
        this.radius = radius;
        this.square = square;
        this.invert = invert;
    }

    public static CanopyGapFilter of(int radius, boolean square, boolean invert) {
        return new CanopyGapFilter(radius, square, invert);
    }

    @Override
    protected boolean shouldPlace(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos pos) {
        for (int x_offset = -radius; x_offset<=radius; x_offset++) {
            for (int z_offset = -radius; z_offset<=radius; z_offset++) {
                // Continue early if we're outside our circular area & we're not checking a square
                if (!square && radius*radius < (x_offset*x_offset + z_offset*z_offset)) continue;
                // Check leaf height vs surface height at this position
                int treetop = context.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX() + x_offset, pos.getZ() + z_offset);
                int surface = context.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX() + x_offset, pos.getZ() + z_offset);
                if (invert ? surface == treetop : treetop > surface) return false;
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public PlacementModifierType<?> type() {
        return NPPlacementModifiers.CANOPY_GAP_FILTER.get();
    }
}
