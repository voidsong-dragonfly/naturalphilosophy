package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.KelpBlock;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;

public class BrownKelpBlock extends KelpBlock {
    public BrownKelpBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected Block getBodyBlock() {
        return NPBlocks.BROWN_KELP_PLANT.get();
    }
}
