package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import voidsong.naturalphilosophy.common.NPBlocks;

import javax.annotation.Nonnull;

public class BrownKelpPlantBlock extends KelpPlantBlock {
    public BrownKelpPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) NPBlocks.BROWN_KELP.get();
    }
}
