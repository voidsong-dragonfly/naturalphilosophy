package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpPlantBlock;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;

import javax.annotation.Nonnull;

public class KelpRootsBlock extends KelpPlantBlock implements GrowingPlantBlockExtension {

    public KelpRootsBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected Block getBodyBlock() {
        return Blocks.KELP_PLANT;
    }

    @Override
    public boolean naturalphilosophy$hasRootsBlock() {
        return true;
    }

    @Override
    public Block naturalphilosophy$getRootsBlock() {
        return this;
    }
}
