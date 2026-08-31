package voidsong.naturalphilosophy.common.worldgen.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.NPTags;
import voidsong.naturalphilosophy.common.blocks.WallFernBlock;
import voidsong.naturalphilosophy.common.worldgen.NPTreeDecorators;
import voidsong.naturalphilosophy.common.worldgen.placementmodifiers.DepthFilter;

import javax.annotation.Nonnull;

public class WallFernDecorator extends TreeDecorator {
    public static final MapCodec<WallFernDecorator> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter(instance -> instance.trunkProbability),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("roots_probability").forGetter(instance -> instance.rootsProbability)
            ).apply(builder, WallFernDecorator::new)
    );
    private final float trunkProbability;
    private final float rootsProbability;

    public WallFernDecorator(float trunkProbability, float rootsProbability) {
        this.trunkProbability = trunkProbability;
        this.rootsProbability = rootsProbability;
    }

    @Override
    @Nonnull
    protected TreeDecoratorType<WallFernDecorator> type() {
        return NPTreeDecorators.WALL_FERN.get();
    }

    @Override
    public void place(@Nonnull Context context) {
        RandomSource random = context.random();
        context.logs().forEach(pos -> {
            if (context.level().isStateAtPosition(pos, state -> state.is(NPTags.Blocks.SUPPORTS_WALL_FERN))) {
                if (random.nextFloat() <= trunkProbability && context.isAir(pos.west())) {
                    context.setBlock(pos.west(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.WEST));
                }

                if (random.nextFloat() <= trunkProbability && context.isAir(pos.east())) {
                    context.setBlock(pos.east(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.EAST));
                }

                if (random.nextFloat() <= trunkProbability && context.isAir(pos.north())) {
                    context.setBlock(pos.north(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.NORTH));
                }

                if (random.nextFloat() <= trunkProbability && context.isAir(pos.south())) {
                    context.setBlock(pos.south(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.SOUTH));
                }
            }
        });

        context.roots().forEach(pos -> {
            if (context.level().isStateAtPosition(pos, state -> state.is(NPTags.Blocks.SUPPORTS_WALL_FERN))) {
                if (random.nextFloat() <= rootsProbability && context.isAir(pos.west())) {
                    context.setBlock(pos.west(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.WEST));
                }

                if (random.nextFloat() <= rootsProbability && context.isAir(pos.east())) {
                    context.setBlock(pos.east(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.EAST));
                }

                if (random.nextFloat() <= rootsProbability && context.isAir(pos.north())) {
                    context.setBlock(pos.north(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.NORTH));
                }

                if (random.nextFloat() <= rootsProbability && context.isAir(pos.south())) {
                    context.setBlock(pos.south(), NPBlocks.WALL_FERN.get().defaultBlockState().setValue(WallFernBlock.FACING, Direction.SOUTH));
                }
            }
        });
    }
}
