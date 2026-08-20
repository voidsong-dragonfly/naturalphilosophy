package voidsong.naturalphilosophy.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.TorchflowerCropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings("unused")
public class TorchFlowerMixin {
    @Mixin(Blocks.class)
    public static class BlocksMixin {
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TorchflowerCropBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=torchflower_crop", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=pitcher_crop", ordinal = 0)))
        private static BlockBehaviour.Properties addLightToTorchFlowerCrop(BlockBehaviour.Properties properties) {
            return properties.lightLevel(state -> state.getValue(TorchflowerCropBlock.AGE) * 7);
        }

        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FlowerBlock;<init>(Lnet/minecraft/core/Holder;FLnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
                slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=torchflower", ordinal = 0),
                        to = @At(value = "CONSTANT", args = "stringValue=poppy", ordinal = 0)))
        private static BlockBehaviour.Properties addLightToTorchFlower(BlockBehaviour.Properties properties) {
            return properties.lightLevel(state -> 14);
        }
    }

    @Mixin(FlowerPotBlock.class)
    public static class FlowerPotBlockMixin {
        @ModifyArg(method = "<init>(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FlowerPotBlock;<init>(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), index = 2)
        private static BlockBehaviour.Properties addOffset(BlockBehaviour.Properties properties, @Local(argsOnly = true) Block potted) {
            return potted.equals(Blocks.TORCHFLOWER) ? properties.lightLevel(state -> 14) : properties;
        }
    }
}
