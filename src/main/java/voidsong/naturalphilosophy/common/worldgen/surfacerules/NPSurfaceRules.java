package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
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

    public static class CliffMaterialCondition extends SurfaceRules.LazyXZCondition {
        public CliffMaterialCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            ChunkAccess chunkaccess = this.context.chunk;
            int i1 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.max(j - 1, 0));
            int j1 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.min(j + 1, 15));
            int i2 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.max(i - 1, 0), j);
            int j2 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.min(i + 1, 15), j);
            if (Math.abs(j1 - i1) > 3 || Math.abs(j2 - i2) > 3) {
                return true;
            } else {
                int i3 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.max(j - 2, 0));
                int j3 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.min(j + 2, 15));
                int i4 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.max(i - 2, 0), j);
                int j4 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.min(i + 2, 15), j);
                return (Math.abs(j3 - i3) > 6 || Math.abs(j4 - i4) > 6);
            }
        }
    }

    public static class CliffLipMaterialCondition extends SurfaceRules.LazyXZCondition {
        public CliffLipMaterialCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            return this.context.stoneDepthBelow <= 2;
        }
    }

    public static class FlatMaterialCondition extends SurfaceRules.LazyXZCondition {
        public FlatMaterialCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            int k = Math.max(j - 1, 0);
            int l = Math.min(j + 1, 15);
            int k1 = Math.max(i - 1, 0);
            int l1 = Math.min(i + 1, 15);
            ChunkAccess chunkaccess = this.context.chunk;
            int i1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, k);
            int j1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, l);
            int i2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k1, j);
            int j2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, l1, j);
            return Math.abs(j2 - i2) == 0 && Math.abs(j1 - i1) == 0 && i1 == i2;
        }
    }
}
