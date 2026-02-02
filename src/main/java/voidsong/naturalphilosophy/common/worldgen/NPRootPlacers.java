package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.rootplacers.MegaRootPlacer;
import voidsong.naturalphilosophy.common.worldgen.rootplacers.RootBallRootPlacer;

public class NPRootPlacers {
    public static final DeferredRegister<RootPlacerType<?>> ROOT_PLACERS = DeferredRegister.create(BuiltInRegistries.ROOT_PLACER_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<RootPlacerType<?>, RootPlacerType<MegaRootPlacer>> MEGA_ROOT_PLACER = ROOT_PLACERS.register("mega_root_placer", () -> new RootPlacerType<>(MegaRootPlacer.CODEC));
    public static final DeferredHolder<RootPlacerType<?>, RootPlacerType<RootBallRootPlacer>> ROOT_BALL_ROOT_PLACER = ROOT_PLACERS.register("root_ball_root_placer", () -> new RootPlacerType<>(RootBallRootPlacer.CODEC));
}
