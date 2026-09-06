package voidsong.naturalphilosophy.common.blocks.interfaces;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.blocks.BrownKelpBlock;
import voidsong.naturalphilosophy.common.blocks.BrownKelpPlantBlock;

public interface GrowingPlantBlockExtension {
    @SuppressWarnings({"ConstantValue", "EqualsBetweenInconvertibleTypes"})
    default boolean naturalphilosophy$hasRootsBlock() {
        return this.getClass().equals(BrownKelpBlock.class) || this.getClass().equals(BrownKelpPlantBlock.class) || this.getClass().equals(KelpBlock.class) || this.getClass().equals(KelpPlantBlock.class);
    }

    @SuppressWarnings({"ConstantValue", "EqualsBetweenInconvertibleTypes"})
    default Block naturalphilosophy$getRootsBlock() {
        if (this.getClass().equals(BrownKelpBlock.class) || this.getClass().equals(BrownKelpPlantBlock.class)) {
            return NPBlocks.BROWN_KELP_ROOTS.get();
        } else if (this.getClass().equals(KelpBlock.class) || this.getClass().equals(KelpPlantBlock.class)) {
            return NPBlocks.KELP_ROOTS.get();
        }
        return Blocks.AIR;
    }
}
