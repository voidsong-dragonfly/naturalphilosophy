package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.features.*;
import voidsong.naturalphilosophy.common.worldgen.features.CustomizableKelpFeature.KelpConfiguration;
import voidsong.naturalphilosophy.common.worldgen.features.CustomWaterPlantFeature.WaterPlantConfiguration;
import voidsong.naturalphilosophy.common.worldgen.features.ArchaeologyBlockFeature.ArchaeologyBlockConfiguration;
import voidsong.naturalphilosophy.common.worldgen.features.CarvedLimitedPoolFeature.CarvedLimitedPoolFeatureConfiguration;
import voidsong.naturalphilosophy.common.worldgen.features.MossPatchFeature.MossPatchConfiguration;

@SuppressWarnings("unused")
public class NPFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<Feature<?>, Feature<ArchaeologyBlockConfiguration>> ARCHAEOLOGY_BLOCK = FEATURES.register("archaeology_block", () -> new ArchaeologyBlockFeature(ArchaeologyBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<SpringConfiguration>> FALLING_SPRING = FEATURES.register("falling_spring", () -> new FallingSpringFeature(SpringConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<KelpConfiguration>> KELP = FEATURES.register("kelp", () -> new CustomizableKelpFeature(KelpConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<WaterPlantConfiguration>> SEAGRASS = FEATURES.register("seaplant", () -> new CustomWaterPlantFeature(WaterPlantConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockStateConfiguration>> ROCK = FEATURES.register("rock", () -> new NonLimitedRockFeature(BlockStateConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<CarvedLimitedPoolFeatureConfiguration>> CARVED_LIMITED_POOL = FEATURES.register("carved_limited_pool", () -> new CarvedLimitedPoolFeature(CarvedLimitedPoolFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<MossPatchConfiguration>> MOSS_PATCH = FEATURES.register("moss_patch", () -> new MossPatchFeature(MossPatchConfiguration.CODEC));
}
