package voidsong.naturalphilosophy.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

import java.util.IdentityHashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
@Mixin(SurfaceRules.class)
public abstract class SurfaceRulesMixin {

    @Mixin(SurfaceRules.ConditionSource.class)
    public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
        @Inject(method = "bootstrap", at = @At("HEAD"))
        private static void onBootstrap(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> registry,
                                        CallbackInfoReturnable<Codec<SurfaceRules.ConditionSource>> cir) {
            SurfaceRules.register(registry, "naturalphilosophy:cliff", NPConditionSources.Cliff.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:flat", NPConditionSources.Flat.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:flat_liquid", NPConditionSources.FlatLiquid.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:climate_sampler", NPConditionSources.ClimateSampler.CODEC);
            SurfaceRules.register(registry, "naturalphilosophy:heightmap_depth", NPConditionSources.HeightmapDepthCheck.CODEC);
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
        @SuppressWarnings("all")
        SurfaceRules.Condition cliff, flat, cliffLip, flatLiquid;
        // Caches for heightmaps& noises
        @Unique
        @SuppressWarnings("all")
        private int oceanHeightmapDepthCache = -Integer.MAX_VALUE;
        @Unique
        @SuppressWarnings("all")
        private IdentityHashMap<ResourceKey<NormalNoise.NoiseParameters>, Double> noiseCache = new IdentityHashMap<>();
        // Update timers for heightmaps, biomes, and noises
        @Unique
        @SuppressWarnings("all")
        private long lastUpdateHeightmapDepth;
        @Unique
        @SuppressWarnings("all")
        private long lastUpdateNoises;

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
            cliff = new NPSurfaceConditions.CliffMaterialCondition(self);
            flat = new NPSurfaceConditions.FlatMaterialCondition(self);
            flatLiquid = new NPSurfaceConditions.FlatLiquidMaterialCondition(self);
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
        public int naturalphilosophy$getOceanHeightmapDepth() {
            if (lastUpdateXZ != lastUpdateHeightmapDepth) {
                oceanHeightmapDepthCache = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockX, blockZ);
                lastUpdateHeightmapDepth = lastUpdateXZ;
            }
            return oceanHeightmapDepthCache;
        }

        @Override
        public double naturalphilosophy$getCachedNoise(ResourceKey<NormalNoise.NoiseParameters> noise) {
            if(lastUpdateXZ != lastUpdateNoises) {
                noiseCache.clear();
                lastUpdateNoises = lastUpdateXZ;
            }
            return noiseCache.computeIfAbsent(noise, v -> randomState.getOrCreateNoise(noise).getValue(blockX, 0.0, blockZ));
        }
    }
}
