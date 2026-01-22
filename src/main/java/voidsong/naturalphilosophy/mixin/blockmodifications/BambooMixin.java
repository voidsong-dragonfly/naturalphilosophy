package voidsong.naturalphilosophy.mixin.blockmodifications;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(BambooStalkBlock.class)
public class BambooMixin extends Block {
    /**
     * This constructor is the default & will be ignored, it exists so we can extend Block
     * @param properties ignored & should not be used!
     */
    public BambooMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.hasProperty(BambooStalkBlock.LEAVES) && state.getValue(BambooStalkBlock.LEAVES).equals(BambooLeaves.LARGE) ? 1 : 0;
    }
}
