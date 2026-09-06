package voidsong.naturalphilosophy.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;

@Mixin(GrowingPlantHeadBlock.class)
public abstract class GrowingPlantHeadBlockMixin extends GrowingPlantBlock {
    /**
     * This constructor is the default & will be ignored, it exists so we can extend Block
     * @param properties ignored & should not be used!
     * @param growthDirection ignored & should not be used!
     * @param shape ignored & should not be used!
     * @param scheduleFluidTicks ignored & should not be used!
     */
    protected GrowingPlantHeadBlockMixin(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks) {
        super(properties, growthDirection, shape, scheduleFluidTicks);
    }

    @ModifyExpressionValue(
            method = "updateShape",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/GrowingPlantHeadBlock;updateBodyAfterConvertedFromHead(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState placeRootsIfAtGround(BlockState original, @Local(argsOnly = true) LevelAccessor level, @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        if (this instanceof GrowingPlantBlockExtension extension && extension.naturalphilosophy$hasRootsBlock()) {
            BlockState below = level.getBlockState(pos.below());
            if (!(below.is(extension.naturalphilosophy$getRootsBlock()) || below.is(getBodyBlock()))) {
                return extension.naturalphilosophy$getRootsBlock().defaultBlockState();
            }
        }
        return original;
    }
}
