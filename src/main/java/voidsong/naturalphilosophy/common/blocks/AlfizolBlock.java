package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class AlfizolBlock extends SnowyDirtBlock {
    public AlfizolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@Nonnull BlockState state, @Nonnull UseOnContext context, @Nonnull ItemAbility itemAbility, boolean simulate) {
        if (context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
            if (itemAbility.equals(ItemAbilities.HOE_TILL)) {
                return Blocks.FARMLAND.defaultBlockState();
            } else if (itemAbility.equals(ItemAbilities.SHOVEL_FLATTEN)) {
                return Blocks.DIRT_PATH.defaultBlockState();
            }
        }
        return null;
    }
}
