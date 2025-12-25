package voidsong.naturalphilosophy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import voidsong.naturalphilosophy.common.blocks.NPProperties;

@SuppressWarnings("unused")
@Mixin(SnowAndFreezeFeature.class)
public abstract class FreezeSurfaceMixin {

    @ModifyArg(method = "place", at = @At(value = "INVOKE", target = "net/minecraft/world/level/WorldGenLevel.getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"), index = 0)
    private Heightmap.Types useCorrectHeightmapType(Heightmap.Types original, int koriginal, int loriginal) {
        return original == Heightmap.Types.MOTION_BLOCKING ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : original;
    }

    @ModifyArg(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), index = 1)
    private BlockState placeCorrectSnowLayer(BlockPos pos, BlockState toPlace, int flags, @Local(name = "worldgenlevel") WorldGenLevel level) {
        if(!toPlace.hasProperty(NPProperties.FEATHERING))
            return toPlace;
        BlockState below = level.getBlockState(pos.below());
        return toPlace.setValue(NPProperties.FEATHERING, !(below.is(BlockTags.SNOW) || below.hasProperty(GrassBlock.SNOWY)));
    }
}

