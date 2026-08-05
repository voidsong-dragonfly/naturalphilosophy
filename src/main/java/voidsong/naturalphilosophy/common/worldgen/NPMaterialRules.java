package voidsong.naturalphilosophy.common.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPRuleSources.*;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPConditionSources.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NPMaterialRules {
    public static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULES = DeferredRegister.create(BuiltInRegistries.MATERIAL_RULE, NaturalPhilosophy.MODID);
    public static final DeferredRegister<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITIONS = DeferredRegister.create(BuiltInRegistries.MATERIAL_CONDITION, NaturalPhilosophy.MODID);

    public static final Supplier<MapCodec<? extends SurfaceRules.RuleSource>> NOISE_THRESHOLD_SELECTOR = MATERIAL_RULES.register("noise_threshold_selector", NoiseThresholdSelectorRuleSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.RuleSource>> RANDOM_THRESHOLD_SELECTOR = MATERIAL_RULES.register("random_threshold_selector", RandomThresholdSelectorRuleSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.RuleSource>> HEIGHT_THRESHOLD_SELECTOR = MATERIAL_RULES.register("height_threshold_selector", HeightThresholdSelectorRuleSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.RuleSource>> STONE_DEPTH_THRESHOLD_SELECTOR = MATERIAL_RULES.register("stone_depth_threshold_selector", StoneDepthThresholdSelectorRuleSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.RuleSource>> BILAYER_FILL = MATERIAL_RULES.register("bilayer_fill", BilayerFillRuleSource.CODEC::codec);

    public static final Supplier<MapCodec<? extends SurfaceRules.ConditionSource>> CLIFF = MATERIAL_CONDITIONS.register("cliff", CliffConditionSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.ConditionSource>> FLAT = MATERIAL_CONDITIONS.register("flat", FlatConditionSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.ConditionSource>> FLAT_LIQUID = MATERIAL_CONDITIONS.register("flat_liquid", FlatLiquidConditionSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.ConditionSource>> UNDERWATER = MATERIAL_CONDITIONS.register("underwater", UnderwaterConditionSource.CODEC::codec);
    public static final Supplier<MapCodec<? extends SurfaceRules.ConditionSource>> CAVE_DEPTH = MATERIAL_CONDITIONS.register("cave_depth", CaveDepthConditionSource.CODEC::codec);
}
