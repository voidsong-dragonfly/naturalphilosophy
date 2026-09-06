package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.level.block.Block;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;

public class BrownKelpRootsBlock extends KelpRootsBlock {

    public BrownKelpRootsBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected Block getBodyBlock() {
        return NPBlocks.BROWN_KELP_PLANT.get();
    }
}
