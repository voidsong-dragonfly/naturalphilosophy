package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.trunkplacers.GiantBambooTrunkPlacer;
import voidsong.naturalphilosophy.common.worldgen.trunkplacers.TallFancyTrunkPlacer;

public class NPTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(BuiltInRegistries.TRUNK_PLACER_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<TallFancyTrunkPlacer>> TALL_FANCY = TRUNK_PLACERS.register("tall_fancy_trunk_placer", () -> new TrunkPlacerType<>(TallFancyTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<GiantBambooTrunkPlacer>> GIANT_BAMBOO = TRUNK_PLACERS.register("giant_bamboo_trunk_placer", () -> new TrunkPlacerType<>(GiantBambooTrunkPlacer.CODEC));
}
