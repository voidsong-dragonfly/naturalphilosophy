package voidsong.naturalphilosophy.common.surfacerules;

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
            int k = Math.max(j - 1, 0);
            int l = Math.min(j + 1, 15);
            ChunkAccess chunkaccess = this.context.chunk;
            int i1 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, k);
            int j1 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, l);
            if (Math.abs(j1 - i1) > 3) {
                return true;
            } else {
                int k1 = Math.max(i - 1, 0);
                int l1 = Math.min(i + 1, 15);
                int i2 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, k1, j);
                int j2 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, l1, j);
                return Math.abs(j2 - i2) > 3;
            }
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
            return Math.abs(j2 - i2) == 0 && Math.abs(j1 - i1) == 0;
        }
    }
}
