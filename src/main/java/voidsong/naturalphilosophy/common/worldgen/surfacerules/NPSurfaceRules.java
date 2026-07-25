package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPRuleSources.AlluvialSedimentsRuleSource.ZonalBoundaries;

import javax.annotation.Nullable;
import java.util.List;

public class NPSurfaceRules {
    record NoiseThresholdSelectorRule(SurfaceRules.Context pContext, ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRule defaultRule, ImmutableList<SurfaceRule> ruleset, List<Double> lowerThresholds, boolean cascade) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the noise we're using for values, and grab our double value
            double d0 = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedNoiseValue(noise, x, z);
            // Iterate through the rules to figure out which rule to provide, and return the rule for the noise bin we're in
            BlockState result = null;
            for(int i = 0; i < Math.min(lowerThresholds.size(), ruleset().size()); i++) {
                if(d0 > lowerThresholds.get(i)) {
                    result = ruleset.get(i).tryApply(x, y, z);
                    // Break the loop if we have gotten a value OR we're not cascading
                    if (result != null || !cascade) break;
                }
            }
            // Return the default rule if we're not in any noise bin or have a noise bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record NoiseThresholdRule(SurfaceRules.Context pContext, ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRule defaultRule, SurfaceRule rule, Double lowerThreshold) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the noise we're using for values, and grab our double value
            double d0 = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedNoiseValue(noise, x, z);
            // Apply the rule and store the result, with null if we are not within the noise bin
            BlockState result = d0 > lowerThreshold ? rule.tryApply(x, y, z) : null;
            // Return the default rule if we're not in the noise bin or have a noise bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record RandomThresholdSelectorRule(SurfaceRules.Context pContext, PositionalRandomFactory positionalRandomFactory, BlockState defaultState, List<BlockState> stateSet, List<Double> lowerThresholds) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the random we're using for values, and grab our double value
            RandomSource randomSource = positionalRandomFactory.at(pContext.blockX, pContext.blockY, pContext.blockZ);
            double d0 = randomSource.nextDouble();
            // Iterate through the rules to figure out which rule to provide, and return the rule for the random bin we're in
            for(int i = 0; i < Math.min(lowerThresholds.size(), stateSet().size()); i++) {
                if(d0 > lowerThresholds.get(i)) return stateSet.get(i);
            }
            // Return the default rule if we're not in any random bin
            return defaultState;
        }
    }

    record RandomThresholdRule(SurfaceRules.Context pContext, PositionalRandomFactory positionalRandomFactory, BlockState defaultState, BlockState state, Double lowerThreshold) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the random we're using for values, and grab our double value
            RandomSource randomSource = positionalRandomFactory.at(pContext.blockX, pContext.blockY, pContext.blockZ);
            // Return the state if we're in the random bin, because blockstates to place can't be null
            if(randomSource.nextDouble() > lowerThreshold) return state;
            // Return the default rule if we're not in the random bin
            return defaultState;
        }
    }

    record HeightThresholdSelectorRule(SurfaceRules.Context pContext, SurfaceRule defaultRule, ImmutableList<SurfaceRule> ruleset, List<Integer> lowerThresholds, int surfaceDepthMultiplier, boolean addStoneDepth, boolean cascade) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the height that we want to compare against
            int comparisonYValue = pContext.blockY + (addStoneDepth ? pContext.stoneDepthAbove : 0) - pContext.surfaceDepth * surfaceDepthMultiplier;
            // Iterate through the rules to figure out which rule to provide, and return the rule for the height bin we're in
            BlockState result = null;
            for(int i = 0; i < Math.min(lowerThresholds.size(), ruleset().size()); i++) {
                if(comparisonYValue > lowerThresholds.get(i)) {
                    result = ruleset.get(i).tryApply(x, y, z);
                    // Break the loop if we have gotten a value OR we're not cascading
                    if (result != null || !cascade) break;
                }
            }
            // Return the default rule if we're not in any height bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record HeightThresholdRule(SurfaceRules.Context pContext, SurfaceRule defaultRule, SurfaceRule rule, int lowerThreshold, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the height that we want to compare against
            int comparisonYValue = pContext.blockY + (addStoneDepth ? pContext.stoneDepthAbove : 0) - pContext.surfaceDepth * surfaceDepthMultiplier;
            // Apply the rule and store the result, with null if we are not within the height bin
            BlockState result = comparisonYValue > lowerThreshold ? rule.tryApply(x, y, z) : null;
            // Return the default rule if we're not in the height bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }


    record StoneDepthThresholdSelectorRule(SurfaceRules.Context pContext, SurfaceRule defaultRule, ImmutableList<SurfaceRule> ruleset, int length) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the rule we want at the specified depth and evaluate it for this position
            BlockState result = pContext.stoneDepthAbove > length ? null : ruleset.get(pContext.stoneDepthAbove - 1).tryApply(x, y, z);
            if(x == 5496 && z == -14064) System.out.println(y + " " + result);
            // Return the default rule if we're not in any depth bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record BilayerFillRule(SurfaceRules.Context pContext, boolean land, int surfaceOffset, int secondaryDepthRange, SurfaceRule topRule, SurfaceRule defaultRule) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check to make sure we are in the correct land/water bin and return null if we fail
            if(land == (pContext.waterHeight != Integer.MIN_VALUE)) return null;
            // Check which bin we're in for surface rules
            if(pContext.stoneDepthAbove <= 1)
                return topRule.tryApply(x, y, z);
            // Calculate the secondary depth we need to check against, zero for no depth; this is after top check for performance
            int secondary = secondaryDepthRange == 0 ? 0 : (int) Mth.map(pContext.getSurfaceSecondary(), -1.0, 1.0, 0.0, secondaryDepthRange);
            // Second bin necessitates more checks to form the 'bottom' effectively
            if(pContext.stoneDepthAbove <= 1 + surfaceOffset + pContext.surfaceDepth + secondary)
                return defaultRule.tryApply(x, y, z);
            // Return a null BlockState in if we fail to be in either bin
            else return null;
        }
    }

    record AlluvialSedimentsRule(SurfaceRules.Context pContext, ZonalBoundaries zonalBoundaries, SurfaceRule riparianRule, SurfaceRule farRiparianRule, SurfaceRule estuaryRule, SurfaceRule coastlineRule) implements SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get biome builder parameters
            double continentalness = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedContinentalnessValue(x, z);
            double peaksValleys = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedPVValue(x, z);
            // Calculate estuary outer & inner boundaries, to allow rivers to taper into estuaries & estuaries to widen
            // Peaks & Valleys is highest at the center of rivers and decreases relatively quickly outwards from there
            double estuaryOuterBoundary = Math.max(zonalBoundaries.farRiparianBoundary(), -0.4f*continentalness + (0.4f*zonalBoundaries.estuaryBoundary() + zonalBoundaries.farRiparianBoundary()));
            double estuaryInnerBoundary = Math.max(-1, -1 + (10*continentalness - 10*zonalBoundaries.estuaryBoundary()));
            // Calculate where estuaries are: within the inner & outer boundaries, not out at sea, and not where the inner boundary is wider than the riparian boundary
            if (continentalness >= zonalBoundaries.oceanBoundary() && peaksValleys >= estuaryInnerBoundary && peaksValleys <= estuaryOuterBoundary && estuaryInnerBoundary < zonalBoundaries.riparianBoundary()) {
                return estuaryRule.tryApply(x, y, z);
            // If we're in a coastline, it takes precedence over rivers
            } else if (continentalness <= zonalBoundaries.coastlineBoundary()) {
                return coastlineRule.tryApply(x, y, z);
            // Otherwise, we're in a riparian ecosystem and should use the riparian rule
            } else if (peaksValleys <= zonalBoundaries.riparianBoundary()) {
                return riparianRule.tryApply(x, y, z);
            // Unless we're so far away that the area will be dryer, in which case we use the far rule
            } else if (peaksValleys <= zonalBoundaries.farRiparianBoundary()) {
                return farRiparianRule.tryApply(x, y, z);
            } else return null;
        }
    }
}
