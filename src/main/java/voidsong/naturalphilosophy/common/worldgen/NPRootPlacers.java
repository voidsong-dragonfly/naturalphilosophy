package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.rootplacers.LargeMangroveRootPlacer;

@SuppressWarnings("unused")
public class NPRootPlacers {
    public static final DeferredRegister<RootPlacerType<?>> ROOT_PLACERS = DeferredRegister.create(BuiltInRegistries.ROOT_PLACER_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<RootPlacerType<?>, RootPlacerType<LargeMangroveRootPlacer>> LARGE_MANGROVE_ROOT_PLACER = ROOT_PLACERS.register("large_mangrove_root_placer", () -> new RootPlacerType<>(LargeMangroveRootPlacer.CODEC));
}
