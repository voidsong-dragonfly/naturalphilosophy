package voidsong.naturalphilosophy.common;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.BaseCoralPlantBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.blocks.DuneGrass;
import voidsong.naturalphilosophy.common.blocks.MycelialGrowthBlock;
import voidsong.naturalphilosophy.common.blocks.PermafrostBlock;
import voidsong.naturalphilosophy.common.blocks.RedAlgaeBlock;
import voidsong.naturalphilosophy.common.blocks.SandyMyceliumBlock;
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
    public static final DeferredBlock<Block> MYCELIAL_GROWTH = BLOCKS.registerBlock("mycelial_growth", MycelialGrowthBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.ROOTS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> RED_ALGAE = BLOCKS.registerBlock("red_algae", RedAlgaeBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.WATER)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.WET_GRASS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> DEAD_BONE_CORAL = BLOCKS.registerBlock("dead_bone_coral", BaseCoralPlantBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .noCollission()
            .instabreak()
    );
    public static final DeferredBlock<Block> DEAD_BONE_CORAL_FAN = BLOCKS.registerBlock("dead_bone_coral_fan", BaseCoralFanBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .noCollission()
            .instabreak()
    );
    public static final DeferredBlock<Block> DEAD_BONE_CORAL_WALL_FAN = BLOCKS.registerBlock("dead_bone_coral_wall_fan", BaseCoralWallFanBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .noCollission()
            .instabreak()
    );
    public static final DeferredBlock<Block> BONE_CORAL = BLOCKS.registerBlock("bone_coral", registerName -> new CoralPlantBlock(
        DEAD_BONE_CORAL.get(),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .noCollission()
            .instabreak()
            .sound(SoundType.WET_GRASS)
            .pushReaction(PushReaction.DESTROY)
        )
    );
    public static final DeferredBlock<Block> BONE_CORAL_FAN = BLOCKS.registerBlock("bone_coral_fan", props -> new CoralFanBlock(
        DEAD_BONE_CORAL_FAN.get(),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .noCollission()
            .instabreak()
            .sound(SoundType.WET_GRASS)
            .pushReaction(PushReaction.DESTROY)
        )
    );
    public static final DeferredBlock<Block> BONE_CORAL_WALL_FAN = BLOCKS.registerBlock("bone_coral_wall_fan", props -> new CoralWallFanBlock(
        DEAD_BONE_CORAL_WALL_FAN.get(),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .noCollission()
            .instabreak()
            .sound(SoundType.WET_GRASS)
            .pushReaction(PushReaction.DESTROY)
        )
    );
    public static final DeferredBlock<Block> BASALTIC_MINERAL_SAND = BLOCKS.registerBlock("basaltic_mineral_sand", props -> new ColoredFallingBlock(new ColorRGBA(-8356741),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.5F)
            .sound(SoundType.SAND)));
    public static final DeferredBlock<Block> SANDY_MYCELIUM = BLOCKS.registerBlock("sandy_mycelium", SandyMyceliumBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PERMAFROST = BLOCKS.registerBlock("permafrost", PermafrostBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .randomTicks()
            .strength(2.8F)
            .sound(SoundType.STONE));
}
