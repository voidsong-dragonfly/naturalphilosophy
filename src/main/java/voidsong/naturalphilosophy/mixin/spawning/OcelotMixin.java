package voidsong.naturalphilosophy.mixin.spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.NPBlocks;

@Mixin(Ocelot.class)
public class OcelotMixin {
    @ModifyExpressionValue(
        method = "checkSpawnObstruction",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")
    )
    private boolean addAlfizolToSpawnBlocks(boolean original, @Local(ordinal = 0) BlockState state) {
        return original || state.is(NPBlocks.ALFIZOL);
    }

    @ModifyExpressionValue(
            method = "checkSpawnObstruction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean removeTreetopSpawns(boolean original, @Local(ordinal = 0) BlockState state, @Local(argsOnly = true) LevelReader level, @Local(ordinal =  0) BlockPos pos) {
        if (state.is(BlockTags.LEAVES)) {
            return level.getBlockStates(new AABB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX(), pos.getY() - 5, pos.getZ())).anyMatch(p -> p.is(BlockTags.DIRT));
        }
        return original;
    }
}
