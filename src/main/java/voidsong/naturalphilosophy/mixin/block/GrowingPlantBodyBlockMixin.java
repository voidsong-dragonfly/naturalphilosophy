package voidsong.naturalphilosophy.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;

import java.util.Optional;

@Mixin(GrowingPlantBodyBlock.class)
public abstract class GrowingPlantBodyBlockMixin extends GrowingPlantBlock {
    /**
     * This constructor is the default & will be ignored, it exists so we can extend Block
     * @param properties ignored & should not be used!
     * @param growthDirection ignored & should not be used!
     * @param shape ignored & should not be used!
     * @param scheduleFluidTicks ignored & should not be used!
     */
    protected GrowingPlantBodyBlockMixin(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks) {
        super(properties, growthDirection, shape, scheduleFluidTicks);
    }

    @Inject(method = "getHeadPos", at = @At("HEAD"), cancellable = true)
    public void addRootsCheckToHeadPos(BlockGetter level, BlockPos pos, Block block, CallbackInfoReturnable<Optional<BlockPos>> cir) {
        if (this instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock() && block == extension.naturalphilosophy$getRootsBlock()) {
            cir.setReturnValue(BlockUtil.getTopConnectedBlock(level, pos.above(), getBodyBlock(), this.growthDirection, this.getHeadBlock()));
        }
    }

    @ModifyExpressionValue(
            method = "updateShape",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 0)
    )
    private boolean doNotReplaceRoots(boolean original, @Local(argsOnly = true, ordinal = 0) BlockState state, @Local(argsOnly = true, ordinal = 1) BlockState facing) {
        return original || (this instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock() && state.is(extension.naturalphilosophy$getRootsBlock()) && facing.is(getBodyBlock()));
    }

}
