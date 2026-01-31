package voidsong.naturalphilosophy.common.worldgen.blockpredicates;

import com.mojang.serialization.Codec;
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

public class CountInRangePredicate implements BlockPredicate {
    protected final BlockPredicate predicate;
    protected final Vec3i offsetStart;
    protected final Vec3i offsetEnd;
    protected final int minCount;
    protected final int maxCount;

    public static final MapCodec<CountInRangePredicate> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(any -> any.predicate),
            Vec3i.offsetCodec(16).optionalFieldOf("start_offset", Vec3i.ZERO).forGetter(any -> any.offsetStart),
            Vec3i.offsetCodec(16).optionalFieldOf("end_offset", Vec3i.ZERO).forGetter(any -> any.offsetEnd),
            Codec.intRange(1, 32768).fieldOf("max_count").forGetter(any -> any.maxCount),
            Codec.intRange(0, 32768).fieldOf("min_count").forGetter(any -> any.minCount)
        ).apply(instance, CountInRangePredicate::new)
    );

    public CountInRangePredicate(BlockPredicate predicate, Vec3i offsetStart, Vec3i offsetEnd, int maxCount, int minCount) {
        this.predicate = predicate;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        MutableBlockPos mutable = new MutableBlockPos();
        int matches = 0;
        for (int x = offsetStart.getX(); x <= offsetEnd.getX(); x++) {
            for (int y = offsetStart.getY(); y <= offsetEnd.getY(); y++) {
                for (int z = offsetStart.getZ(); z <= offsetEnd.getZ(); z++) {
                    if (predicate.test(level, mutable.setWithOffset(pos, x, y, z))) {
                        matches++;
                    }
                }
            }
        }

        return matches > minCount && matches < maxCount;
    }

    @Override
    @Nonnull
    public BlockPredicateType<?> type() {
        return NPBlockPredicates.COUNT_IN_RANGE.get();
    }
}
