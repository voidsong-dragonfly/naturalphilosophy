package voidsong.naturalphilosophy.common.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class PlaceOnWaterOrSelfBlockItem extends PlaceOnWaterBlockItem {

    public PlaceOnWaterOrSelfBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock().asItem().equals(this.asItem())) {
            return this.place(new BlockPlaceContext(context));
        }
        return InteractionResult.PASS;
    }
}
