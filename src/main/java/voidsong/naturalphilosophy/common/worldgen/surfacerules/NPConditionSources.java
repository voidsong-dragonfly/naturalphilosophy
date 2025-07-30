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

public class NPConditionSources {

    public enum CliffConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<CliffConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

    public enum FlatConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

    public enum FlatLiquidConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatLiquidConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

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

    public record UnderwaterConditionSource(boolean shallow) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<UnderwaterConditionSource> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.BOOL.optionalFieldOf("shallow", false).forGetter(UnderwaterConditionSource::shallow)
                ).apply(source, UnderwaterConditionSource::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            class UnderwaterCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    // Exit early if we're above water
                    if (pContext.waterHeight == Integer.MIN_VALUE) return false;
                    // If we don't care about shallowness, return early, else check the Vanilla "shallow water" parameters
                    return !shallow || ((pContext.blockY + pContext.stoneDepthAbove) >= (pContext.waterHeight - 6 - pContext.surfaceDepth));
                }
            }

            return new UnderwaterCondition();
        }
    }

    public record CaveDepthConditionSource(int depth) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<CaveDepthConditionSource> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.INT.fieldOf("depth").forGetter(CaveDepthConditionSource::depth)
                ).apply(source, CaveDepthConditionSource::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            class CaveDepthCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    return ((ContextExtension)(Object)pContext).naturalphilosophy$getOceanHeightmapDepth() - depth >= pContext.blockY;
                }
            }

            return new CaveDepthCondition();
        }
    }

    public static class ExtendedBiomeConditionSource implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<ExtendedBiomeConditionSource> CODEC = KeyDispatchDataCodec.of(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome_is").xmap(ExtendedBiomeConditionSource::new, biomeSource -> biomeSource.biomeSet)
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

        @Override
        @Nonnull
        public String toString() {
            return "ExtendedBiomeConditionSource[biomes=" + this.biomeSet + "]";
        }
    }
}
