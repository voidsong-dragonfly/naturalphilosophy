package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.NPTags;

import javax.annotation.Nonnull;


public class PermafrostBlock extends Block {
    public static final EnumProperty<PermafrostStage> STAGE = EnumProperty.create("stage", PermafrostStage.class);

    public PermafrostBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, PermafrostStage.FROZEN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        // Get the state above us, we only care about the above state for now. It's assumed the other states are too sturdy
        PermafrostStage stage = state.getValue(STAGE);
        BlockState above = null;
        // Grab the average temperature of the blocks, accounting for fluids and high-temperature blocks
        int temperature = 0;
        int increment = 0;
        for (Direction adjacent : Direction.values()) {
            // Prevent loading unloaded chunks when checking neighbor's light and spreading
            if (!level.isAreaLoaded(pos, 1) && adjacent.getAxis().isHorizontal())
                return;
            // Adjacent state
            BlockState nearby = level.getBlockState(pos.relative(adjacent));
            // If this is above, store the above state
            if (adjacent.equals(Direction.UP)) above = nearby;
            // Grab the nearby temperature values, accounting for waterlogging; values are in K
            int blockTemperature = nearby.is(NPTags.Blocks.MELTS_PERMAFROST) || (nearby.is(Blocks.CAMPFIRE) && nearby.getValue(CampfireBlock.LIT)) ? 1300 : 0;
            int fluidTemperature = level.getFluidState(pos.relative(adjacent)).getFluidType().getTemperature();
            // Increment average temperature, and if we have a temperature that's not absolute zero (OOB), increment # of blocks
            temperature += (fluidTemperature + blockTemperature);
            increment += fluidTemperature > 0 ? 1 : 0;
            increment += blockTemperature > 0 ? 1 : 0;
        }
        temperature = increment == 0 ? 0 : temperature/increment;
        BlockState place = canFreeze(above, temperature) ? stage.freeze() : (canMelt(stage, level, pos.above(), above, temperature) ? stage.melt() : state);
        // Check if we want to speed up melting or freezing. If the fluid is below ≈-20C or above ≈100C we accelerate state change.
        boolean speed = (temperature > 0 && (temperature < 250 || temperature > 370));
        // If we changed state, place the new state. Fire-related melting is accelerated compared to other methods
        if (!state.equals(place) && (speed || random.nextInt(10) == 0))
            level.setBlockAndUpdate(pos, place);
    }

    private static boolean canMelt(@Nonnull PermafrostStage stage, @Nonnull ServerLevel level, @Nonnull BlockPos top, @Nonnull BlockState above, int temperature) {
        // If the permafrost is at the melting state AND the average temperature is not greater than ≈50C, do not melt to coarse dirt
        if (stage.equals(PermafrostStage.MELTING) && !(temperature > 320)) {
            return false;
        // Otherwise, early return if the state above is dirt
        } else if (above.is(BlockTags.DIRT)) {
             return true;
        // Otherwise, sky access and temperature
        } else {
            // We melt if the state above is either replaceable or translucent AND the sky can be seen
            // We use "greater than or equal to ≈7C" here as shorthand for if the surroundings would be able to warm the permafrost
            return temperature >= 280 || ((!above.canOcclude() || above.canBeReplaced()) && level.canSeeSky(top) && level.isDay());
        }
    }

    private static boolean canFreeze(@Nonnull BlockState above, int temperature) {
        // We use "lower than ≈-3C" here as shorthand for if the fluid would be able to get enough below freezing to properly solidify permafrost
        // We do not include zero because the empty fluid is absolute zero
        return  (temperature > 0 && temperature < 270) || above.is(NPTags.Blocks.FREEZES_PERMAFROST);
    }

    public enum PermafrostStage implements StringRepresentable {
        MELTING("melting"),
        FROZEN("frozen"),
        SNOWY("snowy");

        private final String name;

        PermafrostStage(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        @Nonnull
        public String getSerializedName() {
            return this.name;
        }

        public BlockState melt() {
            return switch(this) {
                case SNOWY -> NPBlocks.PERMAFROST.get().defaultBlockState().setValue(STAGE, PermafrostStage.FROZEN);
                case FROZEN -> NPBlocks.PERMAFROST.get().defaultBlockState().setValue(STAGE, PermafrostStage.MELTING);
                case MELTING -> Blocks.COARSE_DIRT.defaultBlockState();
            };
        }

        public BlockState freeze() {
            return switch(this) {
                case SNOWY, FROZEN -> NPBlocks.PERMAFROST.get().defaultBlockState().setValue(STAGE, PermafrostStage.SNOWY);
                case MELTING -> NPBlocks.PERMAFROST.get().defaultBlockState().setValue(STAGE, PermafrostStage.FROZEN);
            };
        }
    }
}
