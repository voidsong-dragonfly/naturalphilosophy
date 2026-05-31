package voidsong.naturalphilosophy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("unused")
@Mixin(SnowAndFreezeFeature.class)
public abstract class FreezeSurfaceMixin {

    @ModifyArg(method = "place", at = @At(value = "INVOKE", target = "net/minecraft/world/level/WorldGenLevel.getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"), index = 0)
    private Heightmap.Types useCorrectHeightmapType(Heightmap.Types original, int koriginal, int loriginal) {
        return original == Heightmap.Types.MOTION_BLOCKING ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : original;
    }

    @ModifyArg(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), index = 1)
    private BlockState placeCorrectSnowLayer(BlockPos pos, BlockState toPlace, int flags, @Local(name = "worldgenlevel") WorldGenLevel level, @Local(name = "i1") int height, @Local(name = "k") int k, @Local(name = "l") int l) {
        // Check if we should place two layers (open ground) or one (under trees or on ice)
        boolean two = (!level.getBlockState(pos.below()).is(Blocks.ICE) || !level.getFluidState(pos.below(2)).is(Fluids.WATER)) && height == level.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
        // Return the state we want to place
        return toPlace.is(Blocks.SNOW) ? toPlace.setValue(SnowLayerBlock.LAYERS, two ? 2 : 1) : toPlace;
    }
}

