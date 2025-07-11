package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Climate;
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

    public record ClimateSampler(double tempMin, double tempMax,
                                 double humMin, double humMax,
                                 double contMin, double contMax,
                                 double eroMin, double eroMax,
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
            class ClimateCondition implements SurfaceRules.Condition {
                final Climate.TargetPoint target = pContext.randomState.sampler().sample(pContext.blockX, pContext.blockY, pContext.blockZ);

                @Override
                public boolean test() {
                    boolean temperature = Climate.unquantizeCoord(target.temperature()) >= tempMin && Climate.unquantizeCoord(target.temperature()) <= tempMax;
                    boolean humidity = Climate.unquantizeCoord(target.humidity()) >= humMin && Climate.unquantizeCoord(target.humidity()) <= humMax;
                    boolean continentalness = Climate.unquantizeCoord(target.continentalness()) >= contMin && Climate.unquantizeCoord(target.continentalness()) <= contMax;
                    boolean erosion = Climate.unquantizeCoord(target.erosion()) >= eroMin && Climate.unquantizeCoord(target.erosion()) <= eroMax;
                    boolean weirdness = Climate.unquantizeCoord(target.weirdness()) >= weirdMin && Climate.unquantizeCoord(target.weirdness()) <= weirdMax;
                    boolean depth = Climate.unquantizeCoord(target.depth()) >= depthMin && Climate.unquantizeCoord(target.depth()) <= depthMax;
                    return temperature && humidity && continentalness && erosion && weirdness && depth;
                }
            }

            return new ClimateCondition();
        }
    }

    public record HeightmapDepth(int depth) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<NPSurfaceRules.HeightmapDepth> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.INT.fieldOf("depth").forGetter(NPSurfaceRules.HeightmapDepth::depth)
                ).apply(source, NPSurfaceRules.HeightmapDepth::new)
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
}
