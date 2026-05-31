package voidsong.naturalphilosophy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.config.NPServerConfig;

@Mixin(SnowLayerBlock.class)
public class SnowLayerMixin {
    @Shadow
    @Final
    public static IntegerProperty LAYERS;

    /*
     * These methods exist to make NP draped snow and Vanilla snow equivalent for hand stacking
     */

    @ModifyExpressionValue(method = "canBeReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean useProperReplacement(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPlaceContext useContext) {
        return original || state.is(NPBlocks.DRAPED_SNOW) && useContext.getItemInHand().is(Items.SNOW);
    }

    @ModifyExpressionValue(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean updateAllIncreases(boolean original, @Local() BlockState blockstate) {
        return original || blockstate.is(NPBlocks.DRAPED_SNOW);
    }

    /*
     * These methods exist to ensure NP draped snow will be placed on leaves instead of Vanilla snow
     */

    @ModifyReturnValue(method = "getStateForPlacement", at = @At(value = "RETURN"))
    private BlockState getStateForPlacement(BlockState original, @Local(argsOnly = true) BlockPlaceContext context) {
        // If it's not a snow layer or drooping snow layer, we don't care
        if (!(original.is(Blocks.SNOW) || original.is(NPBlocks.DRAPED_SNOW)))
            return original;
        // If our layer type matches the right type, we also don't care
        BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
        if (original.is(Blocks.SNOW)!=below.is(BlockTags.LEAVES))
            return original;
        // Otherwise, copy over the values we can know (layers) and move on
        return (original.is(Blocks.SNOW) ? NPBlocks.DRAPED_SNOW.get().defaultBlockState() : Blocks.SNOW.defaultBlockState()).setValue(LAYERS, original.getValue(LAYERS));
    }

    @ModifyReturnValue(method = "updateShape", at = @At(value = "RETURN"))
    private BlockState updateShape(BlockState original, @Local(argsOnly = true) Direction facing, @Local(ordinal = 1, argsOnly = true) BlockState facingState) {
        // If it's not a snow layer or drooping snow layer, we don't care; we also don't care if the updated block isn't the downwards one
        if (!(original.is(Blocks.SNOW) || original.is(NPBlocks.DRAPED_SNOW)) || !facing.equals(Direction.DOWN))
            return original;
        // If our layer type matches the right type, we also don't care
        if (original.is(Blocks.SNOW)!=facingState.is(BlockTags.LEAVES))
            return original;
        // Otherwise, copy over the values we can know (layers) and move on
        return (original.is(Blocks.SNOW) ? NPBlocks.DRAPED_SNOW.get().defaultBlockState() : Blocks.SNOW.defaultBlockState()).setValue(LAYERS, original.getValue(LAYERS));
    }

    /*
     * This method ensures snow placed during world generation does not survive if the water beneath it is too deep
     */

    @ModifyReturnValue(method = "canSurvive", at = @At(value = "RETURN"))
    private boolean canSurvive(boolean original, @Local(argsOnly = true) BlockState state, @Local(name = "blockstate") BlockState below, @Local(name = "level") LevelReader levelReader, @Local(name = "pos") BlockPos pos) {
        if ((original && state.is(Blocks.SNOW) || state.is(NPBlocks.DRAPED_SNOW)) && below.is(Blocks.ICE))
            return (levelReader.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - levelReader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ())) < NPServerConfig.maxSnowIceWaterDepth.getAsInt();
        return original;
    }
}
