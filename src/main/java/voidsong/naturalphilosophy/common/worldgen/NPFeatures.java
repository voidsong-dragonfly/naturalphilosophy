package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.features.ArchaeologyBlockFeature;
import voidsong.naturalphilosophy.common.worldgen.features.FallingSpringFeature;
import voidsong.naturalphilosophy.common.worldgen.features.NonRandomSeagrassFeature;
import voidsong.naturalphilosophy.common.worldgen.features.NonShiftedKelpFeature;

@SuppressWarnings("unused")
public class NPFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<Feature<?>, Feature<ArchaeologyBlockFeature.ArchaeologyBlockConfiguration>> ARCHAEOLOGY_BLOCK = FEATURES.register("archaeology_block", () -> new ArchaeologyBlockFeature(ArchaeologyBlockFeature.ArchaeologyBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<SpringConfiguration>> FALLING_SPRING = FEATURES.register("falling_spring", () -> new FallingSpringFeature(SpringConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> KELP = FEATURES.register("kelp", () -> new NonShiftedKelpFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<ProbabilityFeatureConfiguration>> SEAGRASS = FEATURES.register("seagrass", () -> new NonRandomSeagrassFeature(ProbabilityFeatureConfiguration.CODEC));
}
