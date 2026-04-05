package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class GrassyClayHorizonBlock extends GrassBlock {
    public GrassyClayHorizonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@Nonnull BlockState state, @Nonnull UseOnContext context, @Nonnull ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.HOE_TILL)) {
            if (!simulate) {
                Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.CLAY_BALL, 2));
            }
            return Blocks.DIRT.defaultBlockState();
        }
        return null;
    }
}
