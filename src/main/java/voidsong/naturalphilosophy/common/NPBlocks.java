package voidsong.naturalphilosophy.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.blocks.DuneGrass;
import voidsong.naturalphilosophy.common.blocks.TallDuneGrass;
import voidsong.naturalphilosophy.common.blocks.WaterPlantBlock;

public class NPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NaturalPhilosophy.MODID);

    private static final BlockBehaviour.Properties GRASS_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.PLANT)
        .replaceable()
        .noCollission()
        .instabreak()
        .sound(SoundType.GRASS)
        .offsetType(BlockBehaviour.OffsetType.XYZ)
        .ignitedByLava()
        .pushReaction(PushReaction.DESTROY);
    private static final BlockBehaviour.Properties TALL_GRASS_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.PLANT)
        .replaceable()
        .noCollission()
        .instabreak()
        .sound(SoundType.GRASS)
        .offsetType(BlockBehaviour.OffsetType.XZ)
        .ignitedByLava()
        .pushReaction(PushReaction.DESTROY);

    public static final DeferredBlock<Block> DUNE_GRASS = BLOCKS.registerBlock("dune_grass", DuneGrass::new, GRASS_PROPERTIES);
    public static final DeferredBlock<Block> TALL_DUNE_GRASS = BLOCKS.registerBlock("tall_dune_grass", TallDuneGrass::new, TALL_GRASS_PROPERTIES);
    public static final DeferredBlock<Block> RUSHES = BLOCKS.registerBlock("rushes", WaterPlantBlock::new, GRASS_PROPERTIES);
    public static final DeferredBlock<Block> CATTAILS = BLOCKS.registerBlock("cattails", WaterPlantBlock::new, GRASS_PROPERTIES);
}
