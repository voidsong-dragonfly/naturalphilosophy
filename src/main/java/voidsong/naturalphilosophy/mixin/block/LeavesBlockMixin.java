package voidsong.naturalphilosophy.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(LeavesBlock.class)
public class LeavesBlockMixin extends Block {
    /**
     * This constructor is the default & will be ignored, it exists so we can extend Block
     * @param properties ignored & should not be used!
     */
    public LeavesBlockMixin(Properties properties) {
        super(properties);
    }
/*
        @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), index = 0)
        private static BlockBehaviour.Properties addOffset(BlockBehaviour.Properties props) {
            return props.dynamicShape();
        }

        @ModifyReturnValue(method = "getLightBlock", at = @At(value = "RETURN"))
        protected int getLightBlock(int original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
            if (state.is(Blocks.JUNGLE_LEAVES) && !state.getValue(LeavesBlock.PERSISTENT) && level instanceof ProtoChunk chunk && !chunk.getBlockState(pos.above()).is(Blocks.JUNGLE_LEAVES)) {
                return chunk.getBlockState(pos.above()).propagatesSkylightDown(level, pos.above()) ? 0 : original;
            } else return original;
        }*/

    @ModifyReturnValue(method = "getLightBlock", at = @At(value = "RETURN"))
    protected int getLightBlock(int original, @Local(argsOnly = true) BlockState state) {
        if (state.is(Blocks.JUNGLE_LEAVES) && !state.getValue(LeavesBlock.PERSISTENT) && !state.getValue(LeavesBlock.WATERLOGGED)) {
            return state.getValue(LeavesBlock.DISTANCE) > 4 ? 0 : original;
        } else return original;
    }
}

