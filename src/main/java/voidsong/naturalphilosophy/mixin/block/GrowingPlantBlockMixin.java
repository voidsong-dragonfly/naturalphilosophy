package voidsong.naturalphilosophy.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;

@Mixin(GrowingPlantBlock.class)
public abstract class GrowingPlantBlockMixin {
    @Shadow
    protected abstract Block getBodyBlock();

    @ModifyExpressionValue(
            method = "canSurvive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean onlyRootsOrHeadAttachToGround(boolean original, @Local(argsOnly = true) BlockState state, @Local(ordinal = 1) BlockState below) {
        if (this instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock()) {
            return below.is(extension.naturalphilosophy$getRootsBlock()) || !state.is(getBodyBlock()) && original;
        }
        return original;
    }
}
