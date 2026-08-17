package voidsong.naturalphilosophy.mixin.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import voidsong.naturalphilosophy.common.NPTreeGrowers;

@Mixin(Blocks.class)
public class SaplingsMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=oak_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=spruce_sapling", ordinal = 0)))
    private static TreeGrower replaceOakTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.OAK;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=spruce_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=birch_sapling", ordinal = 0)))
    private static TreeGrower replaceSpruceTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.SPRUCE;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=birch_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=jungle_sapling", ordinal = 0)))
    private static TreeGrower replaceBirchTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.BIRCH;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=jungle_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=acacia_sapling", ordinal = 0)))
    private static TreeGrower replaceJungleTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.JUNGLE;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=acacia_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=cherry_sapling", ordinal = 0)))
    private static TreeGrower replaceAcaciaTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.ACACIA;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cherry_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=dark_oak_sapling", ordinal = 0)))
    private static TreeGrower replaceCherryTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.CHERRY;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=dark_oak_sapling", ordinal = 0),
                    to = @At(value = "CONSTANT", args = "stringValue=mangrove_propagule", ordinal = 0)))
    private static TreeGrower replaceDarkOakTreeGrower(TreeGrower grower) {
        return NPTreeGrowers.DARK_OAK;
    }
}
