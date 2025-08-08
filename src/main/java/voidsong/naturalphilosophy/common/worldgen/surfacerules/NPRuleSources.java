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
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class NPRuleSources {
    public record NoiseThresholdSelectorRuleSource(ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRules.RuleSource defaultRule, List<SurfaceRules.RuleSource> ruleset, List<Double> lowerThresholds, boolean cascade) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<NoiseThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdSelectorRuleSource::noise),
                SurfaceRules.RuleSource.CODEC.fieldOf("default_rule").forGetter(NoiseThresholdSelectorRuleSource::defaultRule),
                SurfaceRules.RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(NoiseThresholdSelectorRuleSource::ruleset),
                Codec.DOUBLE.listOf().fieldOf("lower_noise_thresholds").forGetter(NoiseThresholdSelectorRuleSource::lowerThresholds),
                Codec.BOOL.optionalFieldOf("cascade", false).forGetter(NoiseThresholdSelectorRuleSource::cascade)
            ).apply(instance, NoiseThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context pContext) {
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
            for (SurfaceRules.RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new NPSurfaceRules.NoiseThresholdSelectorRule(pContext, noise, defaultRule.apply(pContext), builder.build(), lowerThresholds, cascade);
        }
    }

    public record RandomThresholdSelectorRuleSource(ResourceLocation randomName, Optional<BlockState> defaultState, List<BlockState> stateSet, List<Double> lowerThresholds) implements SurfaceRules.RuleSource {
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
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context pContext) {
            // The random factory can be created outside the rule itself (see VerticalGradientRuleSource)
            final PositionalRandomFactory positionalRandomFactory = pContext.randomState.getOrCreateRandomFactory(this.randomName());
            // Return a new rule with the necessary parameters
            return new NPSurfaceRules.RandomThresholdSelectorRule(pContext, positionalRandomFactory, defaultState.orElse(null), stateSet, lowerThresholds);
        }
    }

    public record HeightThresholdSelectorRuleSource(SurfaceRules.RuleSource defaultRule, List<SurfaceRules.RuleSource> ruleset, List<Integer> lowerThresholds, int surfaceDepthMultiplier, boolean addStoneDepth, boolean cascade) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<HeightThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                SurfaceRules.RuleSource.CODEC.fieldOf("default_rule").forGetter(HeightThresholdSelectorRuleSource::defaultRule),
                SurfaceRules.RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(HeightThresholdSelectorRuleSource::ruleset),
                Codec.INT.listOf().fieldOf("lower_height_thresholds").forGetter(HeightThresholdSelectorRuleSource::lowerThresholds),
                Codec.intRange(-20, 20).optionalFieldOf("surface_depth_multiplier", 0).forGetter(HeightThresholdSelectorRuleSource::surfaceDepthMultiplier),
                Codec.BOOL.optionalFieldOf("add_stone_depth", false).forGetter(HeightThresholdSelectorRuleSource::addStoneDepth),
                Codec.BOOL.optionalFieldOf("cascade", false).forGetter(HeightThresholdSelectorRuleSource::cascade)
            ).apply(instance, HeightThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context pContext) {
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
            for (SurfaceRules.RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new NPSurfaceRules.HeightThresholdSelectorRule(pContext, defaultRule.apply(pContext), builder.build(), lowerThresholds, surfaceDepthMultiplier, addStoneDepth, cascade);
        }
    }

    public record StoneDepthThresholdSelectorRuleSource(SurfaceRules.RuleSource defaultRule, List<SurfaceRules.RuleSource> ruleset) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<StoneDepthThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                SurfaceRules.RuleSource.CODEC.fieldOf("default_rule").forGetter(StoneDepthThresholdSelectorRuleSource::defaultRule),
                SurfaceRules.RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(StoneDepthThresholdSelectorRuleSource::ruleset)
            ).apply(instance, StoneDepthThresholdSelectorRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context pContext) {
            // Follow what SurfaceRules$SequenceRuleSource#apply() does and use an immutable list builder
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
            for (SurfaceRules.RuleSource ruleSource : this.ruleset)
                builder.add(ruleSource.apply(pContext));
            // Return a new rule with the necessary parameters
            return new NPSurfaceRules.StoneDepthThresholdSelectorRule(pContext, defaultRule.apply(pContext), builder.build(), ruleset.size());
        }
    }

    public record BilayerFillRuleSource(boolean land, int surfaceOffset, int secondaryDepthRange, SurfaceRules.RuleSource topRule, SurfaceRules.RuleSource defaultRule) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<BilayerFillRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.BOOL.optionalFieldOf("land", true).forGetter(BilayerFillRuleSource::land),
                Codec.INT.optionalFieldOf("surface_offset", 0).forGetter(BilayerFillRuleSource::surfaceOffset),
                Codec.INT.optionalFieldOf("secondary_depth_range", 0).forGetter(BilayerFillRuleSource::secondaryDepthRange),
                SurfaceRules.RuleSource.CODEC.fieldOf("top_layer").forGetter(BilayerFillRuleSource::topRule),
                SurfaceRules.RuleSource.CODEC.fieldOf("sublayer").forGetter(BilayerFillRuleSource::defaultRule)
            ).apply(instance, BilayerFillRuleSource::new)
        ));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context pContext) {
            // Return a new rule with the necessary parameters
            return new NPSurfaceRules.BilayerFillRule(pContext, land, surfaceOffset, secondaryDepthRange, topRule.apply(pContext), defaultRule.apply(pContext));
        }
    }
}
