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
            // Variable store for future operations
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            ChunkAccess chunk = this.context.chunk;
            int north = Math.max(j - 1, 0);
            int east = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west = Math.max(i - 1, 0);
            // Heightmap heights
            int northHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, north);
            int eastHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, south);
            int westHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, west, j);
            // Get the height difference we need to check to ensure this is a cliff
            int difference = (Math.max(northHeight, Math.max(eastHeight, Math.max(southHeight, westHeight))) - Math.min(northHeight, Math.min(eastHeight, Math.min(southHeight, westHeight))));
            // Exit early, to ensure we don't make the more intensive checks.
            if (difference < 3) return false;
            // Check that we're not on a cliff top-lip. Since the scan is top-down, this catches tops first despite being LazyXZ TODO: possibly not make this apply to big cliff-sides
            boolean lip = this.context.stoneDepthBelow <= 2;
            // Check that we're not at the bottom of a hanging-over cave entrance. These air checks function because, and so this catches the top despite being LazyXZ
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+north)).isAir() && this.context.blockY+2 < northHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX-i+east, this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < eastHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+south)).isAir() && this.context.blockY+2 < southHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX-i+west, this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < westHeight;
            return !lip;
        }
    }

    public static class FlatMaterialCondition extends SurfaceRules.LazyXZCondition {
        public FlatMaterialCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int north = Math.max(j - 1, 0);
            int east = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west = Math.max(i - 1, 0);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunk = this.context.chunk;
            int northHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, north);
            int eastHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, south);
            int westHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, west, j);
            // Check deviation from expected height
            return Math.max(northHeight, Math.max(southHeight, Math.max(westHeight, eastHeight))) - Math.min(northHeight, Math.min(southHeight, Math.min(westHeight, eastHeight))) == 0 && northHeight == chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
        }
    }

    public static class FlatLiquidMaterialCondition extends SurfaceRules.LazyXZCondition {
        public FlatLiquidMaterialCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int k = Math.max(j - 1, 0);
            int l = Math.min(j + 1, 15);
            int k1 = Math.max(i - 1, 0);
            int l1 = Math.min(i + 1, 15);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunkaccess = this.context.chunk;
            int i1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, k);
            int j1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, l);
            int i2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k1, j);
            int j2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, l1, j);
            // Since we scan from the top down, we can ensure we're flat and not going to spill water downwards
            boolean bottom = !chunkaccess.getBlockState(new BlockPos(this.context.blockX, this.context.blockY - 1, this.context.blockZ)).canBeReplaced();
            // Check deviation from expected height
            boolean flat = Math.max(i1, Math.max(j1, Math.max(i2, j2))) - Math.min(i1, Math.min(j1, Math.min(i2, j2))) == 0 && i1 == chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
            // The check to return false on chunk borders is a massive kludge, but I use this with _water_. I can't afford flowing water....
            boolean nonChunkBorder = !(((k == j || l == j)||(k1 == i || l1 == i)) && this.context.blockY > 63);
            // Combine all the checks together
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
            return ((ContextExtension)(Object)this.context).naturalphilosophy$getOceanHeightmapDepth() - depth >= context.blockY;
        }
    }
}
