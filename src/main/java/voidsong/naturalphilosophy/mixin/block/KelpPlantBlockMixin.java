package voidsong.naturalphilosophy.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KelpPlantBlock.class)
public class KelpPlantBlockMixin {
    @Unique
    private static final VoxelShape naturalphilosophy$SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/GrowingPlantBodyBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;Lnet/minecraft/core/Direction;Lnet/minecraft/world/phys/shapes/VoxelShape;Z)V"), index = 0)
    private static BlockBehaviour.Properties addOffset(BlockBehaviour.Properties props) {
        return props.offsetType(BlockBehaviour.OffsetType.XZ);
    }

    @Unique
    @SuppressWarnings({"AddedMixinMembersNamePattern", "unused"})
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return naturalphilosophy$SHAPE.move(vec3.x, vec3.y, vec3.z);
    }
}
