package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nonnull;
import java.util.List;

public class NPRuleSources {
    public record NoiseThresholdSelectorRuleSource(ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRules.RuleSource defaultRule, List<SurfaceRules.RuleSource> ruleset, List<Double> lowerThresholds) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<NoiseThresholdSelectorRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdSelectorRuleSource::noise),
                SurfaceRules.RuleSource.CODEC.fieldOf("default_rule").forGetter(NoiseThresholdSelectorRuleSource::defaultRule),
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
            // Check the noise we're using for values, and grab our double value
            double d0 = pContext.randomState.getOrCreateNoise(this.noise).getValue(pContext.blockX, 0.0, pContext.blockZ);
            // Iterate through the rules to figure out which rule to provide, and return the rule for the noise bin we're in
            for(int i = 0; i < Math.min(lowerThresholds.size(), ruleset().size()); i++) {
                if(d0 > lowerThresholds.get(i)) return ruleset.get(i).apply(pContext);
            }
            // Return the default rule if we're not in any noise bin
            return defaultRule.apply(pContext);
        }
    }

    public record BilayerFillRuleSource(int surfaceOffset, int secondaryDepthRange, SurfaceRules.RuleSource topRule, SurfaceRules.RuleSource defaultRule) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<BilayerFillRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.INT.fieldOf("surface_offset").forGetter(BilayerFillRuleSource::surfaceOffset),
                Codec.INT.fieldOf("secondary_depth_range").forGetter(BilayerFillRuleSource::secondaryDepthRange),
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
            // Check to make sure we're above water, and return a specialty BlockState rule if we fail
            if(pContext.waterHeight != Integer.MIN_VALUE) return new NullStateRule();
            // Calculate the secondary depth we need to check against, zero for no depth
            int secondary = secondaryDepthRange == 0 ? 0 : (int) Mth.map(pContext.getSurfaceSecondary(), -1.0, 1.0, 0.0, secondaryDepthRange);
            // Check which bin we're in for surface rules
            if(pContext.stoneDepthAbove <= 1) return topRule.apply(pContext);
            else if(pContext.stoneDepthAbove <= 1 + surfaceOffset + pContext.surfaceDepth + secondary) return defaultRule.apply(pContext);
            // Return a null BlockState in if we fail to be in either bin
            else return new NullStateRule();
        }

        private record NullStateRule() implements SurfaceRules.SurfaceRule {
            @Override
            public BlockState tryApply(int x, int y, int z) {
                return null;
            }
        }
    }
}
