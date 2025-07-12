package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import javax.annotation.Nonnull;

public class NPSurfaceRules {

    public enum Cliff implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPSurfaceRules.Cliff> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

    public enum CliffLip implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPSurfaceRules.CliffLip> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getCliffLip();
        }
    }

    public enum Flat implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<NPSurfaceRules.Flat> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

        public static final KeyDispatchDataCodec<NPSurfaceRules.FlatLiquid> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

    public record ClimateSampler(double tempMin,  double tempMax,
                                 double humMin,   double humMax,
                                 double contMin,  double contMax,
                                 double eroMin,   double eroMax,
                                 double weirdMin, double weirdMax,
                                 double depthMin, double depthMax) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<NPSurfaceRules.ClimateSampler> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                        Codec.DOUBLE.optionalFieldOf("min_temperature", -1.0).forGetter(NPSurfaceRules.ClimateSampler::tempMin),
                        Codec.DOUBLE.optionalFieldOf("max_temperature", 1.0).forGetter(NPSurfaceRules.ClimateSampler::tempMax),
                        Codec.DOUBLE.optionalFieldOf("min_humidity", -1.0).forGetter(NPSurfaceRules.ClimateSampler::humMin),
                        Codec.DOUBLE.optionalFieldOf("max_humidity", 1.0).forGetter(NPSurfaceRules.ClimateSampler::humMax),
                        Codec.DOUBLE.optionalFieldOf("min_continentalness", -1.0).forGetter(NPSurfaceRules.ClimateSampler::contMin),
                        Codec.DOUBLE.optionalFieldOf("max_continentalness", 1.0).forGetter(NPSurfaceRules.ClimateSampler::contMax),
                        Codec.DOUBLE.optionalFieldOf("min_erosion", -1.0).forGetter(NPSurfaceRules.ClimateSampler::eroMin),
                        Codec.DOUBLE.optionalFieldOf("max_erosion", 1.0).forGetter(NPSurfaceRules.ClimateSampler::eroMax),
                        Codec.DOUBLE.optionalFieldOf("min_weirdness", -1.0).forGetter(NPSurfaceRules.ClimateSampler::weirdMin),
                        Codec.DOUBLE.optionalFieldOf("max_weirdness", 1.0).forGetter(NPSurfaceRules.ClimateSampler::weirdMax),
                        Codec.DOUBLE.optionalFieldOf("min_depth", -Double.MAX_VALUE).forGetter(NPSurfaceRules.ClimateSampler::depthMin),
                        Codec.DOUBLE.optionalFieldOf("max_depth", Double.MAX_VALUE).forGetter(NPSurfaceRules.ClimateSampler::depthMax)
                    ).apply(source, NPSurfaceRules.ClimateSampler::new)
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
            class BiomeCondition extends SurfaceRules.LazyYCondition {
                BiomeCondition() {
                    super(pContext);
                }

                @Override
                protected boolean compute() {
                    return biomeSet.contains(context.biome.get());
                }
            }

            return new BiomeCondition();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                return other instanceof ExtendedBiomeConditionSource source && this.biomeSet.equals(source.biomeSet);
            }
        }

        @Override
        public int hashCode() {
            return this.biomeSet.hashCode();
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
