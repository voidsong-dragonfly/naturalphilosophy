package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlamingBromeliadBlock extends FlowerBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty NEEDS_RANDOM_TICK = BooleanProperty.create("needs_random_tick");

    public FlamingBromeliadBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
        super(effect, seconds, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, true).setValue(NEEDS_RANDOM_TICK, true));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, NEEDS_RANDOM_TICK);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return state.is(BlockTags.LOGS) || state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(NEEDS_RANDOM_TICK, false);
    }

    @Override
    protected void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        level.setBlockAndUpdate(pos, state.setValue(NEEDS_RANDOM_TICK, false));
        tick(state.setValue(NEEDS_RANDOM_TICK, false), level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(NEEDS_RANDOM_TICK);
    }

    @Override
    protected void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, getTickDelay(level.random));
    }

    @Override
    protected void tick(@Nonnull BlockState state, ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        level.scheduleTick(pos, this, getTickDelay(level.random));
        boolean open = state.getValue(OPEN);
        if (open && level.getSkyDarken() > 9){
            level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
        } else if (!open && 9 > level.getSkyDarken()) {
            level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
        }
    }

    private static int getTickDelay(RandomSource random) {
        return 160 + random.nextInt(40);
    }
}
