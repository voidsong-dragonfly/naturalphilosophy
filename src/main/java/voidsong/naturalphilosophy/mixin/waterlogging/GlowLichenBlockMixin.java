package voidsong.naturalphilosophy.mixin.waterlogging;

import net.minecraft.world.level.block.GlowLichenBlock;
import org.spongepowered.asm.mixin.Mixin;
import voidsong.naturalphilosophy.common.blocks.interfaces.FragileWaterloggedBlock;

@Mixin(GlowLichenBlock.class)
@SuppressWarnings("unused")
public class GlowLichenBlockMixin implements FragileWaterloggedBlock {
    @Override
    public boolean naturalphilosophy$canBeReplaced(Object block) {
        return block.getClass().equals(GlowLichenBlock.class);
    }
}
