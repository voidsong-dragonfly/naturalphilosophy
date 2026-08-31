package voidsong.naturalphilosophy.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import voidsong.naturalphilosophy.common.NPBlocks;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (context.getItemInHand().getItem() == Items.FERN) {
            BlockState wall = NPBlocks.WALL_FERN.get().getStateForPlacement(context);
            BlockPos pos = context.getClickedPos();

            if (wall != null && context.getClickedFace().getAxis().isHorizontal()) {
                if (wall.canSurvive(context.getLevel(), pos) && context.getLevel().isUnobstructed(wall, pos, CollisionContext.empty())) {
                    cir.setReturnValue(wall);
                }
            }
        }
    }
}
