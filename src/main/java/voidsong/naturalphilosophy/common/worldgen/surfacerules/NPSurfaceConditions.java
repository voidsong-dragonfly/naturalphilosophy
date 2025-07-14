package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class NPSurfaceConditions {
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
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            ChunkAccess chunkaccess = this.context.chunk;
            boolean bottom3 = chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+Math.max(j - 1, 0))).isAir() && this.context.blockY+2 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.max(j - 1, 0));
            boolean bottom4 = chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+Math.min(j + 1, 15))).isAir() && this.context.blockY+2 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.min(j + 1, 15));
            boolean bottom1 = chunkaccess.getBlockState(new BlockPos(this.context.blockX-i+Math.max(i - 1, 0), this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.max(i - 1, 0), j);
            boolean bottom2 = chunkaccess.getBlockState(new BlockPos(this.context.blockX-i+Math.min(i + 1, 15), this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.min(i + 1, 15), j);

            boolean bottomLip = (bottom1 || bottom2 || bottom3 || bottom4);

            if(!bottomLip) {
                bottom3 = chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+4, this.context.blockZ-j+Math.max(j - 2, 0))).isAir() && this.context.blockY+4 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.max(j - 2, 0));
                bottom4 = chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+4, this.context.blockZ-j+Math.min(j + 2, 15))).isAir() && this.context.blockY+4 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, Math.min(j + 2, 15));
                bottom1 = chunkaccess.getBlockState(new BlockPos(this.context.blockX-i+Math.max(i - 2, 0), this.context.blockY+4, this.context.blockZ)).isAir() && this.context.blockY+4 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.max(i - 2, 0), j);
                bottom2 = chunkaccess.getBlockState(new BlockPos(this.context.blockX-i+Math.min(i + 2, 15), this.context.blockY+4, this.context.blockZ)).isAir() && this.context.blockY+4 < chunkaccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, Math.min(i + 2, 15), j);
                bottomLip = (bottom1 || bottom2 || bottom3 || bottom4);
            }

            return this.context.stoneDepthBelow <= 2 || bottomLip;
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
            return (Math.max(i1, Math.max(j1, Math.max(i2, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2)))) == 0 && i1 == chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
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
            boolean bottom = !chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY - 1, this.context.blockZ)).canBeReplaced();
            boolean flat = Math.max(i1, Math.max(j1, Math.max(i2, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2))) == 0 && i1 == chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
            // The check to return false on chunk borders is a massive kludge, but I use this with _water_. I can't afford flowing water....
            boolean nonChunkBorder = !(((k == j || l == j)||(k1 == i || l1 == i)) && this.context.blockY > 63);
            return flat && bottom && nonChunkBorder;
        }
    }

    public static class ClimateSamplerCondition extends SurfaceRules.LazyYCondition {
        private final long tempMin, tempMax, humMin, humMax, contMin, contMax, eroMin, eroMax, weirdMin, weirdMax, depthMin, depthMax;
        final Climate.TargetPoint target;

        public ClimateSamplerCondition(SurfaceRules.Context context,
                                       long tempMin,  long tempMax,
                                       long humMin,   long humMax,
                                       long contMin,  long contMax,
                                       long eroMin,   long eroMax,
                                       long weirdMin, long weirdMax,
                                       long depthMin, long depthMax) {
            super(context);
            this.tempMin  = tempMin;  this.tempMax  = tempMax;
            this.humMin   = humMin;   this.humMax   = humMax;
            this.contMin  = contMin;  this.contMax  = contMax;
            this.eroMin   = eroMin;   this.eroMax   = eroMax;
            this.weirdMin = weirdMin; this.weirdMax = weirdMax;
            this.depthMin = depthMin; this.depthMax = depthMax;
            this.target = context.randomState.sampler().sample(context.blockX, context.blockY, context.blockZ);
        }

        @Override
        protected boolean compute() {
            boolean temperature = target.temperature() >= tempMin && target.temperature() <= tempMax;
            boolean humidity = target.humidity() >= humMin && target.humidity() <= humMax;
            boolean continentalness = target.continentalness() >= contMin && target.continentalness() <= contMax;
            boolean erosion = target.erosion() >= eroMin && target.erosion() <= eroMax;
            boolean weirdness = target.weirdness() >= weirdMin && target.weirdness() <= weirdMax;
            boolean depth = target.depth() >= depthMin && target.depth() <= depthMax;
            return temperature && humidity && continentalness && erosion && weirdness && depth;
        }
    }

    public static class HeightmapDepthCondition extends SurfaceRules.LazyYCondition {
        private final int depth;

        public HeightmapDepthCondition(SurfaceRules.Context context, int depth) {
            super(context);
            this.depth = depth;
        }

        @Override
        protected boolean compute() {
            return context.chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, context.blockX, context.blockZ) - depth >= context.blockY;
        }
    }
}
