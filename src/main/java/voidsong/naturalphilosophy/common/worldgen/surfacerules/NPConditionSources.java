package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;

import javax.annotation.Nonnull;

public class NPConditionSources {

    public enum Cliff implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPConditionSources.Cliff> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getCliff();
        }
    }

    public enum Flat implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPConditionSources.Flat> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getFlat();
        }
    }

    public enum FlatLiquid implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPConditionSources.FlatLiquid> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getFlatLiquid();
        }
    }

    public record ClimateSampler(long tempMin,  long tempMax,
                                 long humMin,   long humMax,
                                 long contMin,  long contMax,
                                 long eroMin,   long eroMax,
                                 long weirdMin, long weirdMax,
                                 long depthMin, long depthMax) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<NPConditionSources.ClimateSampler> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                        Codec.FLOAT.optionalFieldOf("min_temperature", -1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::tempMin),
                        Codec.FLOAT.optionalFieldOf("max_temperature", 1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::tempMax),
                        Codec.FLOAT.optionalFieldOf("min_humidity", -1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::humMin),
                        Codec.FLOAT.optionalFieldOf("max_humidity", 1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::humMax),
                        Codec.FLOAT.optionalFieldOf("min_continentalness", -1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::contMin),
                        Codec.FLOAT.optionalFieldOf("max_continentalness", 1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::contMax),
                        Codec.FLOAT.optionalFieldOf("min_erosion", -1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::eroMin),
                        Codec.FLOAT.optionalFieldOf("max_erosion", 1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::eroMax),
                        Codec.FLOAT.optionalFieldOf("min_weirdness", -1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::weirdMin),
                        Codec.FLOAT.optionalFieldOf("max_weirdness", 1.0f).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::weirdMax),
                        Codec.FLOAT.optionalFieldOf("min_depth", -Float.MAX_VALUE).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::depthMin),
                        Codec.FLOAT.optionalFieldOf("max_depth", Float.MAX_VALUE).xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(NPConditionSources.ClimateSampler::depthMax)
                    ).apply(source, NPConditionSources.ClimateSampler::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return new NPSurfaceConditions.ClimateSamplerCondition(pContext, tempMin, tempMax, humMin, humMax, contMin, contMax, eroMin, eroMax, weirdMin, weirdMax, depthMin, depthMax);
        }
    }

    public record HeightmapDepthCheck(int depth) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<HeightmapDepthCheck> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.INT.fieldOf("depth").forGetter(HeightmapDepthCheck::depth)
                ).apply(source, HeightmapDepthCheck::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return new NPSurfaceConditions.HeightmapDepthCondition(pContext, depth);
        }
    }

    public static class ExtendedBiomeConditionSource implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<ExtendedBiomeConditionSource> CODEC = KeyDispatchDataCodec.of(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome_is").xmap(ExtendedBiomeConditionSource::makeBiomeConditionSource, biomeSource -> biomeSource.biomeSet)
        );
        public final HolderSet<Biome> biomeSet;

        public ExtendedBiomeConditionSource(HolderSet<Biome> biomes) {
            this.biomeSet = biomes;
        }

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context pContext) {
            class BiomeCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    return biomeSet.contains(pContext.biome.get());
                }
            }

            return new BiomeCondition();
        }

        private static ExtendedBiomeConditionSource makeBiomeConditionSource(HolderSet<Biome> biomes) {
            return new ExtendedBiomeConditionSource(biomes);
        }

        @Override
        @Nonnull
        public String toString() {
            return "ExtendedBiomeConditionSource[biomes=" + this.biomeSet + "]";
        }
    }
}
