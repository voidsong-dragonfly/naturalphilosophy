package voidsong.naturalphilosophy.common.blocks;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import voidsong.naturalphilosophy.common.blocks.interfaces.GrowingPlantBlockExtension;

import javax.annotation.Nonnull;

public class KelpRootsBlock extends KelpPlantBlock implements GrowingPlantBlockExtension {
    public static final EnumProperty<RootsSize> SIZE = EnumProperty.create("size", RootsSize.class);

    public KelpRootsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SIZE, RootsSize.SMALL));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SIZE);
    }

    @Override
    @Nonnull
    protected Block getBodyBlock() {
        return Blocks.KELP_PLANT;
    }

    @Override
    public boolean naturalphilosophy$hasRootsBlock() {
        return true;
    }

    @Override
    public Block naturalphilosophy$getRootsBlock() {
        return this;
    }

    public enum RootsSize implements StringRepresentable {
        SMALL("small"),
        LARGE("large");

        private final String name;

        private RootsSize(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
