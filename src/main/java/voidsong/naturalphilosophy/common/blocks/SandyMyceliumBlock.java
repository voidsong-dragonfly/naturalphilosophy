package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;

public class SandyMyceliumBlock extends MyceliumBlock {
    public SandyMyceliumBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!canBeGrass(state, level, pos)) {
            if (!level.isAreaLoaded(pos, 1))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            level.setBlockAndUpdate(pos, NPBlocks.SANDY_MYCELIUM.get().defaultBlockState());
        } else {
            super.randomTick(state, level, pos, random);
        }
    }
}
