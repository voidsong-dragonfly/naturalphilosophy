package voidsong.naturalphilosophy.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import voidsong.naturalphilosophy.common.blocks.NPProperties;

@Mixin(SnowLayerBlock.class)
@SuppressWarnings("unused")
public class SnowLayerMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SnowLayerBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"), index = 0)
    private static BlockState addFeatheringToConstructor(BlockState defaultState) {
        return defaultState.setValue(NPProperties.FEATHERING, true);
    }

    @Inject(method = "createBlockStateDefinition", at = @At(value = "RETURN"))
    private void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(NPProperties.FEATHERING);
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At(value = "RETURN"))
    private BlockState getStateForPlacement(BlockState original, @Local(argsOnly = true) BlockPlaceContext context) {
        BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
        return original.setValue(NPProperties.FEATHERING, !(below.is(BlockTags.SNOW) || below.hasProperty(GrassBlock.SNOWY)));
    }

    @ModifyReturnValue(method = "updateShape", at = @At(value = "RETURN"))
    private BlockState updateShape(BlockState original, @Local(argsOnly = true) Direction facing, @Local(ordinal = 1, argsOnly = true) BlockState facingState) {
        if (original.hasProperty(NPProperties.FEATHERING) && facing.equals(Direction.DOWN))
            return original.setValue(NPProperties.FEATHERING, !(facingState.is(BlockTags.SNOW) || facingState.hasProperty(GrassBlock.SNOWY)));
        return original;
    }
}
