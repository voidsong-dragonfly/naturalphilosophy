package voidsong.naturalphilosophy.common.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.blockpredicates.AnyInRangePredicate;
import voidsong.naturalphilosophy.common.worldgen.blockpredicates.CountInRangePredicate;

import javax.annotation.Nonnull;

public class NPBlockPredicates {
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATES = DeferredRegister.create(BuiltInRegistries.BLOCK_PREDICATE_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<AnyInRangePredicate>> ANY_IN_RANGE = BLOCK_PREDICATES.register("any_in_range", () -> new BlockPredicateType<>() {
        @Override
        @Nonnull
        public MapCodec<AnyInRangePredicate> codec() {
            return AnyInRangePredicate.CODEC;
        }
    });
    public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<CountInRangePredicate>> COUNT_IN_RANGE = BLOCK_PREDICATES.register("count_in_range", () -> new BlockPredicateType<>() {
        @Override
        @Nonnull
        public MapCodec<CountInRangePredicate> codec() {
            return CountInRangePredicate.CODEC;
        }
    });
}
