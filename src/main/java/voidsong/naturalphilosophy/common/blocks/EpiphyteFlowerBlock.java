package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class EpiphyteFlowerBlock extends FlowerBlock {
    public EpiphyteFlowerBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
        super(effect, seconds, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.is(BlockTags.LOGS) || state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock;
    }
}
