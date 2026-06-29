package voidsong.naturalphilosophy.common.worldgen.surfacerules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class NPSurfaceConditions {
    public static class CliffCondition extends SurfaceRules.LazyXZCondition {
        public CliffCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Variable store for future operations
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            ChunkAccess chunk = this.context.chunk;
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Heightmap heights
            int northHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, west, j);
            // Get adjacent heightmap min & max heights. We include this block in max but not min to keep spires but not pits as stone
            int min = Math.min(northHeight, Math.min(eastHeight, Math.min(southHeight, westHeight)));
            int maxAdjacent = min;
            if (j != north) maxAdjacent = northHeight;
            if (i != east)  maxAdjacent = Math.max(maxAdjacent, eastHeight);
            if (j != south) maxAdjacent = Math.max(maxAdjacent, southHeight);
            if (i != west)  maxAdjacent = Math.max(maxAdjacent, westHeight);
            int y = context.blockY + context.stoneDepthAbove - 1;
            int max = Math.max(y, maxAdjacent);
            // Use the height difference we need to check to ensure this is a cliff (max - min >= 3) or a one-block spire (max > maxAdjacent)
            if ((max - min) < 3 && !(max > maxAdjacent && (max - min) == 2)) return false;
            // Return if we're on a cliff lip (low depth, flat height profile) based on the top of this block column if run at the surface
            // Note: this check will cause strange behavior if it is not run in the top "section" first to allow LazyXZ to cache it for the entire column
            if (this.context.stoneDepthBelow <= 3 && (max - y < 2))  return false;
            // Return if we're at in the middle of a large cliff, regardless of depth, to prevent 3D cliffs from having grass streaks due to lip-like behavior
            if ((max - y) > 11 && (y - min) > 1) return true;
            // Check that we're not at the bottom of a hanging-over cave entrance by checking if there's air pockets beneath ground height on the sides
            // Note: this has the same strange behavior & cause as the previous lip check does, it should be run in the top "section" first and allowed to cache itself
            boolean floor =  chunk.getBlockState(new BlockPos(this.context.blockX, y+2, this.context.blockZ-j+north)).isAir() && y+2 < northHeight;
            floor = floor || chunk.getBlockState(new BlockPos(this.context.blockX-i+east, y+2, this.context.blockZ)).isAir() && y+2 < eastHeight;
            floor = floor || chunk.getBlockState(new BlockPos(this.context.blockX, y+2, this.context.blockZ-j+south)).isAir() && y+2 < southHeight;
            floor = floor || chunk.getBlockState(new BlockPos(this.context.blockX-i+west, y+2, this.context.blockZ)).isAir() && y+2 < westHeight;
            return !floor;
        }
    }

    public static class FlatCondition extends SurfaceRules.LazyXZCondition {
        public FlatCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunk = this.context.chunk;
            int northHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, west, j);
            // Check deviation from expected height
            return Math.max(northHeight, Math.max(southHeight, Math.max(westHeight, eastHeight))) - Math.min(northHeight, Math.min(southHeight, Math.min(westHeight, eastHeight))) == 0 && northHeight == chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
        }
    }

    public static class FlatLiquidCondition extends SurfaceRules.LazyXZCondition {
        public FlatLiquidCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunk = this.context.chunk;
            int northHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, west, j);
            // Since we scan from the top down, we can ensure we're flat and not going to spill water downwards
            boolean bottom = !chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY - 1, this.context.blockZ)).canBeReplaced();
            // Check deviation from expected height
            boolean flat = Math.max(northHeight, Math.max(southHeight, Math.max(westHeight, eastHeight))) - Math.min(northHeight, Math.min(southHeight, Math.min(westHeight, eastHeight))) == 0 && northHeight == chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
            // The check to return false on chunk borders is a massive kludge, but I use this with _water_. I can't afford flowing water....
            boolean nonChunkBorder = !(((north == j || south == j)||(west == i || east == i)) && this.context.blockY > 63);
            // Combine all the checks together
            return flat && bottom && nonChunkBorder;
        }
    }

    public record UnderwaterCondition(SurfaceRules.Context context, boolean shallow) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            // Exit early if we're above water
            if (context.waterHeight == Integer.MIN_VALUE) return false;
            // If we don't care about shallowness, return early, else check the Vanilla "shallow water" parameters
            return !shallow || ((context.blockY + context.stoneDepthAbove) >= (context.waterHeight - 6 - context.surfaceDepth));
        }
    }

    public record CaveDepthCondition(SurfaceRules.Context context, int depth) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            int heightmapDepth = context.chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, context.blockX, context.blockZ);
            // Return early if this isn't a cave - ie, if the ground above is solid
            if (context.stoneDepthAbove >= (heightmapDepth-context.blockY+1)) return false;
            // Return early if we're above the necessary depth
            if (heightmapDepth - depth <= context.blockY) return false;
            // If we're shallower than twelve blocks, we do not need to check the air blocks above this block
            // We remove/add stoneDepthAbove to make sure we stay congruous with the top block of the cave
            int currentDepth = heightmapDepth - context.blockY + context.stoneDepthAbove;
            if (currentDepth < 12) return true;
            // Check to make sure we're not underneath a massive overhang by checking if greater than 3/4ths what's above is air
            ContextExtension eContext = ((ContextExtension)(Object)context);
            if ((eContext.naturalphilosophy$getLastYBeforeCurrentCavern()-(context.blockY+context.stoneDepthAbove)) < (currentDepth*3)/4) return true;
            // If we are under an overhang, but are on the side of a mini-cliff, this should also be a cave floor
            int searchLevel = context.blockY + context.stoneDepthAbove- 2;
            return eContext.naturalphilosophy$getCachedCaveLipValue(searchLevel);
        }
    }
}
