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

public class FlatnessFilter extends PlacementFilter {
    public static final MapCodec<FlatnessFilter> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.fieldOf("radius").forGetter(r -> r.radius),
                Codec.BOOL.optionalFieldOf("square", false).forGetter(r -> r.square)
        ).apply(builder, FlatnessFilter::new)
    );
    private final int radius;
    private final boolean square;

    private FlatnessFilter(int radius, boolean square) {
        this.radius = radius;
        this.square = square;
    }

    public static FlatnessFilter of(int radius, boolean square) {
        return new FlatnessFilter(radius, square);
    }

    @Override
    protected boolean shouldPlace(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos pos) {
        for (int x_offset = -radius; x_offset<=radius; x_offset++) {
            for (int z_offset = -radius; z_offset<=radius; z_offset++) {
                // Continue early if we're outside our circular area & we're not checking a square
                if (!square && radius*radius < (x_offset*x_offset + z_offset*z_offset)) continue;
                // Check surface height vs placement height at this position
                if (context.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX() + x_offset, pos.getZ() + z_offset) > pos.getY() + 3) return false;
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public PlacementModifierType<?> type() {
        return NPPlacementModifiers.FLATNESS_FILTER.get();
    }
}
