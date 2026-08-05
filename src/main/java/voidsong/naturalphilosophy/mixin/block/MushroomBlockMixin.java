package voidsong.naturalphilosophy.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomBlock.class)
public class MushroomBlockMixin {
    @Shadow
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BushBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), index = 0)
    private static BlockBehaviour.Properties addOffset(BlockBehaviour.Properties props) {
        return props.offsetType(BlockBehaviour.OffsetType.XZ);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Vec3 vec3 = state.getOffset(level, pos);
        cir.setReturnValue(SHAPE.move(vec3.x, vec3.y, vec3.z));
    }
}
