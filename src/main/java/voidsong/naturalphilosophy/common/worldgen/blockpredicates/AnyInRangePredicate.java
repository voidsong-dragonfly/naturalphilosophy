package voidsong.naturalphilosophy.common.worldgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import voidsong.naturalphilosophy.common.worldgen.NPBlockPredicates;

import javax.annotation.Nonnull;

public class AnyInRangePredicate implements BlockPredicate {
    protected final BlockPredicate predicate;
    protected final Vec3i offsetStart;
    protected final Vec3i offsetEnd;

    public static final MapCodec<AnyInRangePredicate> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(any -> any.predicate),
            Vec3i.offsetCodec(16).optionalFieldOf("start_offset", Vec3i.ZERO).forGetter(any -> any.offsetStart),
            Vec3i.offsetCodec(16).optionalFieldOf("end_offset", Vec3i.ZERO).forGetter(any -> any.offsetEnd)
        ).apply(instance, AnyInRangePredicate::new)
    );

    public AnyInRangePredicate(BlockPredicate predicate, Vec3i offsetStart, Vec3i offsetEnd) {
        this.predicate = predicate;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        MutableBlockPos mutable = new MutableBlockPos();
        for (int x = offsetStart.getX(); x <= offsetEnd.getX(); x++) {
            for (int y = offsetStart.getY(); y <= offsetEnd.getY(); y++) {
                for (int z = offsetStart.getZ(); z <= offsetEnd.getZ(); z++) {
                    if (predicate.test(level, mutable.setWithOffset(pos, x, y, z))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    @Nonnull
    public BlockPredicateType<?> type() {
        return NPBlockPredicates.ANY_IN_RANGE.get();
    }
}
