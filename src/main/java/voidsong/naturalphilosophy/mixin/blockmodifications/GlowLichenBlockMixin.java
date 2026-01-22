package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(GlowLichenBlock.class)
@SuppressWarnings("unused")
public class GlowLichenBlockMixin {
    @Unique
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER || ((fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) && !state.getValue(BlockStateProperties.WATERLOGGED));
    }


    @Unique
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE), 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }
            return true;
        } else if (fluidState.is(Fluids.LAVA) || fluidState.is(Fluids.FLOWING_LAVA)){
            Fluid type = fluidState.getType();
            if (type instanceof LavaFluid lava)
                lava.beforeDestroyingBlock(level, pos, state);
            level.setBlock(pos, fluidState.createLegacyBlock(), 3);
            return true;
        } else
            return false;
    }
}
