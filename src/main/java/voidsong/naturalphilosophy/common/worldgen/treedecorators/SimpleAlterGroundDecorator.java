package voidsong.naturalphilosophy.common.worldgen.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import voidsong.naturalphilosophy.common.worldgen.NPTreeDecorators;

import javax.annotation.Nonnull;
import java.util.List;

public class SimpleAlterGroundDecorator extends TreeDecorator {
    public static final MapCodec<SimpleAlterGroundDecorator> CODEC = RecordCodecBuilder.mapCodec(
    builder -> builder.group(
        BlockStateProvider.CODEC.fieldOf("provider").forGetter(instance -> instance.provider),
        Codec.BOOL.fieldOf("remove_grass").forGetter(instance -> instance.removeGrass)
        ).apply(builder, SimpleAlterGroundDecorator::new)
    );
    private final BlockStateProvider provider;
    private final boolean removeGrass;

    public SimpleAlterGroundDecorator(BlockStateProvider provider, boolean removeGrass) {
        this.provider = provider;
        this.removeGrass = removeGrass;
    }

    @Override
    @Nonnull
    protected TreeDecoratorType<?> type() {
        return NPTreeDecorators.SIMPLE_ALTER_GROUND.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        List<BlockPos> origins = Lists.newArrayList();
        List<BlockPos> roots = context.roots();
        List<BlockPos> logs = context.logs();
        if (roots.isEmpty()) {
            origins.add(logs.getFirst());
        } else if (!logs.isEmpty() && (roots.getFirst().getY() == logs.getFirst().getY() || logs.size() > 8)) {
            origins.add(logs.getFirst());
            origins.addAll(roots);
        } else {
            origins.addAll(roots);
        }

        if (!origins.isEmpty()) {
            var eventProvider = EventHooks.alterGround(context, origins, this.provider::getState);
            int i = origins.getFirst().getY();
            origins.stream().filter(pos -> pos.getY() == i || pos.getY() == i-1).forEach(pos -> this.placeCircle(context, pos, eventProvider));
        }
    }

    private void placeCircle(TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider) {
        int radius = context.roots().isEmpty() || context.logs().size() < 10 ? 1 : 2;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (Math.abs(i) != radius || Math.abs(j) != radius) {
                    this.placeBlockAt(context, pos.offset(i, 0, j), eventProvider);
                }
            }
        }
    }

    private void placeBlockAt(TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider) {
        // Return early if we're too far below the surface
        if (!context.isAir(pos.above(3)) && !context.level().isStateAtPosition(pos.above(3), state -> state.is(BlockTags.LEAVES))) return;
        // Modified version of the Minecraft version of this function; adds the option to replace grass above
        for (int i = 2; i >= -3; i--) {
            BlockPos above = pos.above(i);
            if (Feature.isGrassOrDirt(context.level(), above)) {
                context.setBlock(above, eventProvider.getState(context.random(), pos));
                // Added portion to replace grass
                if (removeGrass) {
                    if (context.level().isStateAtPosition(above.above(), state -> state.is(Blocks.SHORT_GRASS)))
                        context.setBlock(above.above(), Blocks.AIR.defaultBlockState());
                    else if (context.level().isStateAtPosition(above.above(), state -> state.is(Blocks.TALL_GRASS))) {
                        context.setBlock(above.above(), Blocks.AIR.defaultBlockState());
                        context.setBlock(above.above().above(), Blocks.AIR.defaultBlockState());
                    }
                }
                break;
            }

            if (!context.isAir(above) && i < 0) {
                break;
            }
        }
    }
}
