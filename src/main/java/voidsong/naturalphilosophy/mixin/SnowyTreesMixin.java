package voidsong.naturalphilosophy.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

/**
 * Inspired by code from Apollounknowndev in Simple Snowy Fix, and updated to 1.20.4
 */
@SuppressWarnings("unused")
@Mixin(TreeFeature.class)
public class SnowyTreesMixin {
    @Inject(
        method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z",
        locals = LocalCapture.CAPTURE_FAILHARD,
        at = @At(value= "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;encapsulatingPositions(Ljava/lang/Iterable;)Ljava/util/Optional;", shift = At.Shift.BEFORE)
    )
    private void snowOnLeaves(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir, WorldGenLevel level, RandomSource random, BlockPos pos, TreeConfiguration treeConfiguration, Set<BlockPos> set, Set<BlockPos> set2, Set<BlockPos> set3, Set<BlockPos> set4) {
        for (BlockPos leaves : set3) {
            BlockPos top = leaves.above();
            if (level.isEmptyBlock(top) && level.getBiome(top).value().shouldSnow(level, top)) {
                level.setBlock(top, Blocks.SNOW.defaultBlockState(), 19);
            }
        }
    }
}