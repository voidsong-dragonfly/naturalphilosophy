package voidsong.naturalphilosophy.common.blocks.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is an extension of {@link net.minecraft.world.level.block.SimpleWaterloggedBlock}.
 * It provides the ability to have the waterlogged block be popped off when fluids flow into it, similar to torches or
 * buttons in Vanilla, via a mixin into {@link net.minecraft.world.level.material.FlowingFluid#spreadTo(LevelAccessor, BlockPos, BlockState, Direction, FluidState)}
 */
public interface FragileWaterloggedBlock extends SimpleWaterloggedBlock {
    @Override
    default boolean canPlaceLiquid(@Nullable Player player, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Fluid fluid) {
        return true;
    }

    boolean naturalphilosophy$canBeReplaced(Object block);
}