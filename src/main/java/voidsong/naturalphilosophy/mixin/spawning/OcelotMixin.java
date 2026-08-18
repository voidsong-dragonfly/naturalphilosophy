package voidsong.naturalphilosophy.mixin.spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import voidsong.naturalphilosophy.common.NPBlocks;

@Mixin(Ocelot.class)
public class OcelotMixin {
    @ModifyExpressionValue(
        method = "checkSpawnObstruction",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")
    )
    private boolean allowFragileReplacement(boolean original, @Local(ordinal = 0) BlockState state) {
        return original || state.is(NPBlocks.ALFIZOL);
    }
}
