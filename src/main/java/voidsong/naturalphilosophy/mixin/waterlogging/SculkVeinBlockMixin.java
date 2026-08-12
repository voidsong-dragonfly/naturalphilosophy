package voidsong.naturalphilosophy.mixin.waterlogging;

import net.minecraft.world.level.block.SculkVeinBlock;
import org.spongepowered.asm.mixin.Mixin;
import voidsong.naturalphilosophy.common.blocks.interfaces.FragileWaterloggedBlock;

@Mixin(SculkVeinBlock.class)
public class SculkVeinBlockMixin implements FragileWaterloggedBlock {
    @Override
    public boolean naturalphilosophy$canBeReplaced(Object block) {
        return block.getClass().equals(SculkVeinBlock.class);
    }
}
