package voidsong.naturalphilosophy.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPRuleSources;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPSurfaceConditions;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.NPConditionSources;
import voidsong.naturalphilosophy.common.worldgen.surfacerules.ContextExtension;

import java.util.function.Function;

@SuppressWarnings("unused")
@Mixin(SurfaceRules.class)
public abstract class SurfaceRulesMixin {

    @Mixin(SurfaceRules.ConditionSource.class)
    public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
        @Inject(method = "bootstrap", at = @At("HEAD"))
        private static void onBootstrap(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> registry,
                                        CallbackInfoReturnable<Codec<SurfaceRules.ConditionSource>> cir) {
            SurfaceRules.register(registry, "naturalphilosophy:cliff", NPConditionSources.CliffConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:flat", NPConditionSources.FlatConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:flat_liquid", NPConditionSources.FlatLiquidConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:land_top_layer", NPConditionSources.LandTopLayerConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:underwater", NPConditionSources.UnderwaterConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:cave_depth", NPConditionSources.CaveDepthConditionSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:biome", NPConditionSources.ExtendedBiomeConditionSource.CODEC);
        }
    }

    @Mixin(SurfaceRules.RuleSource.class)
    public interface RuleSource extends Function<SurfaceRules.Context, SurfaceRules.SurfaceRule> {
        @Inject(method = "bootstrap", at = @At("HEAD"))
        private static void onBootstrap(Registry<MapCodec<? extends SurfaceRules.RuleSource>> registry,
                                        CallbackInfoReturnable<MapCodec<? extends SurfaceRules.RuleSource>> cir) {
            SurfaceRules.register(registry, "naturalphilosophy:noise_threshold_selector", NPRuleSources.NoiseThresholdSelectorRuleSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:random_threshold_selector", NPRuleSources.RandomThresholdSelectorRuleSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:height_threshold_selector", NPRuleSources.HeightThresholdSelectorRuleSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:stone_depth_threshold_selector", NPRuleSources.StoneDepthThresholdSelectorRuleSource.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:bilayer_fill", NPRuleSources.BilayerFillRuleSource.CODEC);
        }
    }

    @Mixin(SurfaceRules.Context.class)
    protected static final class Context implements ContextExtension {
        // Shadowed variables from Context
        @Shadow long lastUpdateXZ;
        @Shadow public int blockX;
        @Shadow public int blockZ;
        @Shadow @Final public ChunkAccess chunk;
        @Shadow @Final public RandomState randomState;
        // Variables for the cached conditions
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private SurfaceRules.Condition cliff;
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private SurfaceRules.Condition flat;
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private SurfaceRules.Condition flatLiquid;
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private SurfaceRules.Condition aboveWater;
        // Caches for noise values & the last update value for it
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private long lastUpdateNoiseCache;
        @Unique
        @SuppressWarnings("AddedMixinMembersNamePattern")
        private final Object2DoubleArrayMap<ResourceKey<NormalNoise.NoiseParameters>> noiseCache = new Object2DoubleArrayMap<>();

        @Inject(method="<init>", at=@At("RETURN"))
        public void instantiateConditions(SurfaceSystem system,
                                          RandomState randomState,
                                          ChunkAccess chunk,
                                          NoiseChunk noiseChunk,
                                          Function<BlockPos, Holder<Biome>> biomeGetter,
                                          Registry<Biome> biomeRegistry,
                                          WorldGenerationContext context,
                                          CallbackInfo ci) {
            SurfaceRules.Context self = (SurfaceRules.Context) (Object) this;
            cliff = new NPSurfaceConditions.CliffCondition(self);
            flat = new NPSurfaceConditions.FlatCondition(self);
            flatLiquid = new NPSurfaceConditions.FlatLiquidCondition(self);
            aboveWater = new NPSurfaceConditions.LandTopLayerCondition(self);
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getCliff() {
            return cliff;
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getFlat() {
            return flat;
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getFlatLiquid() {
            return flatLiquid;
        }

        @Override
        public SurfaceRules.Condition naturalphilosophy$getLandTopLayer() {
            return aboveWater;
        }

        @Override
        public double naturalphilosophy$getCachedNoiseValue(ResourceKey<NormalNoise.NoiseParameters> noise, int x, int z) {
            if (lastUpdateXZ != lastUpdateNoiseCache) {
                noiseCache.clear();
                lastUpdateNoiseCache = lastUpdateXZ;
            }
            return noiseCache.computeIfAbsent(noise, key -> randomState.getOrCreateNoise(noise).getValue(x, 0.0, z));
        }
    }
}
