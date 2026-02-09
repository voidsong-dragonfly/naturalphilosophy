package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BushFlowerMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=peony", ordinal = 0)))
    private static BlockBehaviour.Properties changePeonySpeedFactor(BlockBehaviour.Properties properties) {
        return properties.speedFactor(0.01F).strength(0.3f);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=rose_bush", ordinal = 0)))
    private static BlockBehaviour.Properties changeRoseSpeedFactor(BlockBehaviour.Properties properties) {
        return properties.speedFactor(0.01F).strength(0.3f);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TallFlowerBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=lilac", ordinal = 0)))
    private static BlockBehaviour.Properties changeLilacSpeedFactor(BlockBehaviour.Properties properties) {
        return properties.speedFactor(0.01F).strength(0.3f);
    }
}
