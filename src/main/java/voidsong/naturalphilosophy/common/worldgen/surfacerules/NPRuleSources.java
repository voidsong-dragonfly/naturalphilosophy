package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.NoiseThresholdSelectorRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.NoiseThresholdRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.RandomThresholdSelectorRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.RandomThresholdRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.HeightThresholdSelectorRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.HeightThresholdRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.StoneDepthThresholdSelectorRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.BilayerFillRule;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceRules.AlluvialSedimentsRule;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class NPRuleSources {
    public record NoiseThresholdSelectorRuleSource(ResourceKey<NormalNoise.NoiseParameters> noise, RuleSource defaultRule, List<RuleSource> ruleset, List<Double> lowerThresholds, boolean cascade) implements RuleSource {
        public static final KeyDispatchDataCodec<NoiseThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdSelectorRuleSource::noise),
                RuleSource.CODEC.optionalFieldOf("default_rule", new PassthroughRuleSource()).forGetter(NoiseThresholdSelectorRuleSource::defaultRule),
                RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(NoiseThresholdSelectorRuleSource::ruleset),
                Codec.DOUBLE.listOf().fieldOf("lower_noise_thresholds").forGetter(NoiseThresholdSelectorRuleSource::lowerThresholds),
                Codec.BOOL.optionalFieldOf("cascade", false).forGetter(NoiseThresholdSelectorRuleSource::cascade)
            ).apply(instance, NoiseThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // Check if we have a singleton list for lower thresholds && ruleset
            if (ruleset.size() == 1 && lowerThresholds.size() == 1)
                return new NoiseThresholdRule(pContext, noise, defaultRule.apply(pContext), ruleset.getFirst().apply(pContext), lowerThresholds.getFirst());
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRule> builder = ImmutableList.builder();
            for (RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new NoiseThresholdSelectorRule(pContext, noise, defaultRule.apply(pContext), builder.build(), lowerThresholds, cascade);
        }
    }

    public record RandomThresholdSelectorRuleSource(ResourceLocation randomName, Optional<BlockState> defaultState, List<BlockState> stateSet, List<Double> lowerThresholds) implements RuleSource {
        public static final KeyDispatchDataCodec<RandomThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("random_name").forGetter(RandomThresholdSelectorRuleSource::randomName),
                BlockState.CODEC.optionalFieldOf("default_state").forGetter(RandomThresholdSelectorRuleSource::defaultState),
                BlockState.CODEC.listOf().fieldOf("state_set").forGetter(RandomThresholdSelectorRuleSource::stateSet),
                Codec.DOUBLE.listOf().fieldOf("lower_random_thresholds").forGetter(RandomThresholdSelectorRuleSource::lowerThresholds)
            ).apply(instance, RandomThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // The random factory can be created outside the rule itself (see VerticalGradientRuleSource)
            final PositionalRandomFactory positionalRandomFactory = pContext.randomState.getOrCreateRandomFactory(this.randomName());
            // Check if we have a singleton list for lower thresholds && ruleset
            if (stateSet.size() == 1 && lowerThresholds.size() == 1)
                return new RandomThresholdRule(pContext, positionalRandomFactory, defaultState.orElse(null), stateSet.getFirst(), lowerThresholds.getFirst());
            // Return a new rule with the necessary parameters
            return new RandomThresholdSelectorRule(pContext, positionalRandomFactory, defaultState.orElse(null), stateSet, lowerThresholds);
        }
    }

    public record HeightThresholdSelectorRuleSource(RuleSource defaultRule, List<RuleSource> ruleset, List<Integer> lowerThresholds, int surfaceDepthMultiplier, boolean addStoneDepth, boolean cascade) implements RuleSource {
        public static final KeyDispatchDataCodec<HeightThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                RuleSource.CODEC.optionalFieldOf("default_rule", new PassthroughRuleSource()).forGetter(HeightThresholdSelectorRuleSource::defaultRule),
                RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(HeightThresholdSelectorRuleSource::ruleset),
                Codec.INT.listOf().fieldOf("lower_height_thresholds").forGetter(HeightThresholdSelectorRuleSource::lowerThresholds),
                Codec.intRange(-20, 20).optionalFieldOf("surface_depth_multiplier", 0).forGetter(HeightThresholdSelectorRuleSource::surfaceDepthMultiplier),
                Codec.BOOL.optionalFieldOf("add_stone_depth", false).forGetter(HeightThresholdSelectorRuleSource::addStoneDepth),
                Codec.BOOL.optionalFieldOf("cascade", false).forGetter(HeightThresholdSelectorRuleSource::cascade)
            ).apply(instance, HeightThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // Check if we have a singleton list for lower thresholds && ruleset
            if (ruleset.size() == 1 && lowerThresholds.size() == 1)
                return new HeightThresholdRule(pContext, defaultRule.apply(pContext), ruleset.getFirst().apply(pContext), lowerThresholds.getFirst(), surfaceDepthMultiplier, addStoneDepth);
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRule> builder = ImmutableList.builder();
            for (RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new HeightThresholdSelectorRule(pContext, defaultRule.apply(pContext), builder.build(), lowerThresholds, surfaceDepthMultiplier, addStoneDepth, cascade);
        }
    }

    public record StoneDepthThresholdSelectorRuleSource(RuleSource defaultRule, List<RuleSource> ruleset) implements RuleSource {
        public static final KeyDispatchDataCodec<StoneDepthThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                RuleSource.CODEC.optionalFieldOf("default_rule", new PassthroughRuleSource()).forGetter(StoneDepthThresholdSelectorRuleSource::defaultRule),
                RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(StoneDepthThresholdSelectorRuleSource::ruleset)
            ).apply(instance, StoneDepthThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRule> builder = ImmutableList.builder();
            for (RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new StoneDepthThresholdSelectorRule(pContext, defaultRule.apply(pContext), builder.build(), ruleset.size());
        }
    }

    public record BilayerFillRuleSource(boolean land, int surfaceOffset, int secondaryDepthRange, RuleSource topRule, RuleSource defaultRule) implements RuleSource {
        public static final KeyDispatchDataCodec<BilayerFillRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.BOOL.optionalFieldOf("land", true).forGetter(BilayerFillRuleSource::land),
                Codec.INT.optionalFieldOf("surface_offset", 0).forGetter(BilayerFillRuleSource::surfaceOffset),
                Codec.INT.optionalFieldOf("secondary_depth_range", 0).forGetter(BilayerFillRuleSource::secondaryDepthRange),
                RuleSource.CODEC.fieldOf("top_layer").forGetter(BilayerFillRuleSource::topRule),
                RuleSource.CODEC.fieldOf("sublayer").forGetter(BilayerFillRuleSource::defaultRule)
            ).apply(instance, BilayerFillRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // Return a new rule with the necessary parameters
            return new BilayerFillRule(pContext, land, surfaceOffset, secondaryDepthRange, topRule.apply(pContext), defaultRule.apply(pContext));
        }
    }

    public record AlluvialSedimentsRuleSource(ZonalBoundaries zonalBoundaries, RuleSource riparianRule, RuleSource farRiparianRule, RuleSource estuaryRule, RuleSource coastlineRule) implements RuleSource {
        public static final KeyDispatchDataCodec<AlluvialSedimentsRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ZonalBoundaries.CODEC.optionalFieldOf("zonal_boundaries", new ZonalBoundaries(-0.19, -0.11, 0.03, 0.4, 0.0)).forGetter(AlluvialSedimentsRuleSource::zonalBoundaries),
                        RuleSource.CODEC.fieldOf("riparian").forGetter(AlluvialSedimentsRuleSource::riparianRule),
                        RuleSource.CODEC.fieldOf("far_riparian").forGetter(AlluvialSedimentsRuleSource::farRiparianRule),
                        RuleSource.CODEC.fieldOf("estuary").forGetter(AlluvialSedimentsRuleSource::estuaryRule),
                        RuleSource.CODEC.fieldOf("coastline").forGetter(AlluvialSedimentsRuleSource::coastlineRule)
                ).apply(instance, AlluvialSedimentsRuleSource::new)
        ));

        public record ZonalBoundaries(double oceanBoundary, double coastlineBoundary, double estuaryBoundary, double riparianBoundary, double farRiparianBoundary) {
            public static final Codec<ZonalBoundaries> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.doubleRange(-1, 1).fieldOf("ocean_boundary").forGetter(placement -> placement.oceanBoundary),
                            Codec.doubleRange(-1, 1).fieldOf("coastline_boundary").forGetter(placement -> placement.coastlineBoundary),
                            Codec.doubleRange(-1, 1).fieldOf("estuary_boundary").forGetter(placement -> placement.estuaryBoundary),
                            Codec.doubleRange(-1, 1).fieldOf("riparian_boundary").forGetter(placement -> placement.riparianBoundary),
                            Codec.doubleRange(-1, 1).fieldOf("far_riparian_boundary").forGetter(placement -> placement.farRiparianBoundary)
                    ).apply(instance, ZonalBoundaries::new)
            );
        }
        /* The default ZonalBoundaries are as follows:
         *  "zonal_boundaries": {
         *    "ocean_boundary": -0.19,
         *    "coastline_boundary": -0.11,
         *    "estuary_boundary": -0.05,
         *    "riparian_boundary": -0.65,
         *    "far_riparian_boundary": -0.45
         *  },
         */

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            // Return a new rule with the necessary parameters
            return new AlluvialSedimentsRule(pContext, zonalBoundaries, riparianRule.apply(pContext), farRiparianRule.apply(pContext), estuaryRule.apply(pContext), coastlineRule.apply(pContext));
        }
    }

    private static class PassthroughRuleSource implements RuleSource {
        @Override
        @SuppressWarnings("all")
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return null;
        }

        public SurfaceRule apply(SurfaceRules.Context pContext) {
            return (x, y, z) -> null;
        }
    }
}
