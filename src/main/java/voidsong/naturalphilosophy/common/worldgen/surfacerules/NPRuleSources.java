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

public class NPRuleSources {
    public record NoiseThresholdSelectorRuleSource(ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRules.RuleSource defaultRule, List<SurfaceRules.RuleSource> ruleset, List<Double> lowerThresholds) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<NoiseThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdSelectorRuleSource::noise),
                SurfaceRules.RuleSource.CODEC.optionalFieldOf("default_rule", null).forGetter(NoiseThresholdSelectorRuleSource::defaultRule),
                SurfaceRules.RuleSource.CODEC.listOf().fieldOf("ruleset").forGetter(NoiseThresholdSelectorRuleSource::ruleset),
                Codec.DOUBLE.listOf().fieldOf("lower_noise_thresholds").forGetter(NoiseThresholdSelectorRuleSource::lowerThresholds)
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
            return new NPSurfaceRules.NoiseThresholdSelectorRule(pContext, noise, defaultRule.apply(pContext), builder.build(), lowerThresholds);
        }
    }

    public record RandomThresholdSelectorRuleSource(ResourceLocation randomName, BlockState defaultState, List<BlockState> stateSet, List<Double> lowerThresholds) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<RandomThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("random_name").forGetter(RandomThresholdSelectorRuleSource::randomName),
                BlockState.CODEC.optionalFieldOf("default_state", null).forGetter(RandomThresholdSelectorRuleSource::defaultState),
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
            return new NPSurfaceRules.RandomThresholdSelectorRule(pContext, positionalRandomFactory, defaultState, stateSet, lowerThresholds);
        }
    }

    public record BilayerFillRuleSource(int surfaceOffset, int secondaryDepthRange, SurfaceRules.RuleSource topRule, SurfaceRules.RuleSource defaultRule) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<BilayerFillRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
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
            return new NPSurfaceRules.BilayerFillRule(pContext, surfaceOffset, secondaryDepthRange, topRule.apply(pContext), defaultRule.apply(pContext));
        }
    }
}
