package voidsong.naturalphilosophy.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import voidsong.naturalphilosophy.common.NPTags;

@Mixin(TallFlowerBlock.class)
public class TallFlowerBlockMixin extends DoublePlantBlock {
    /**
     * This constructor is the default & will be ignored, it exists so we can extend DoublePlantBlock
     * @param properties ignored & should not be used!
     */
    public TallFlowerBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        // Only spread if this is the right block to do so
        if (state.is(NPTags.Blocks.SPREADING_BUSH_FLOWERS)) {
            BlockPos center = state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? pos.below() : pos;
            // Search in a 5x3x5 area for places to grow, and if valid, place
            for (BlockPos search : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
                if (this.canSurvive(this.defaultBlockState(), level, search) && level.getBlockState(search).isAir() && level.getBlockState(search.above()).isAir() && random.nextInt(5) == 0) {
                    level.setBlockAndUpdate(search, defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
                    level.setBlockAndUpdate(search.above(), defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
                }
            }
            ci.cancel();
        }
    }
}
