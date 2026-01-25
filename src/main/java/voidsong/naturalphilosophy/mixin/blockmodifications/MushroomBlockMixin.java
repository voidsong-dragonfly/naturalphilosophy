package voidsong.naturalphilosophy.mixin.blockmodifications;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import voidsong.naturalphilosophy.common.NPTags;

@Mixin(MushroomBlock.class)
public class MushroomBlockMixin {
    @Unique
    private static final VoxelShape naturalphilosophy$SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0);

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BushBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), index = 0)
    private static BlockBehaviour.Properties addOffset(BlockBehaviour.Properties props) {
        return props.offsetType(BlockBehaviour.OffsetType.XZ);
    }

    @ModifyReturnValue(method = "getShape", at = @At(value = "RETURN"))
    public VoxelShape getShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        return  state.is(NPTags.Blocks.WIDE_PLANTS) ? naturalphilosophy$SHAPE.move(vec3.x, vec3.y, vec3.z) : original;
    }
}
