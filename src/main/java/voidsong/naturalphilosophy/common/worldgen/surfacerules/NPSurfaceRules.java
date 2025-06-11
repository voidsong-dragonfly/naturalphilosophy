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
            if ((Math.max(i1, Math.max(j1, Math.max(i1, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2)))) > 3) {
                return true;
            } else {
                int i3 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.max(j - 2, 0));
                int j3 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.min(j + 2, 15));
                int i4 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.max(i - 2, 0), j);
                int j4 = chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.min(i + 2, 15), j);
                return ((Math.max(i3, Math.max(j3, Math.max(i4, j4))) - Math.min(i3, Math.min(j3, Math.min(i4, j4)))) > 6);
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
            return (Math.max(i1, Math.max(j1, Math.max(i2, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2)))) == 0;
        }
    }

    public static class FlatLiquidMaterialCondition extends SurfaceRules.LazyXZCondition {
        public FlatLiquidMaterialCondition(SurfaceRules.Context context) {
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
            // The check to return false on chunk borders is a massive kludge, but I use this with _water_. I can't afford flowing water....
            return Math.max(i1, Math.max(j1, Math.max(i2, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2))) == 0 && !(((k == j || l == j)||(k1 == i || l1 == i))&&this.context.blockY>63);
        }
    }
}
