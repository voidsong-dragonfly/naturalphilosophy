package voidsong.naturalphilosophy.common;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.blocks.*;

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
    public static final DeferredBlock<Block> RUSHES = BLOCKS.registerBlock("rushes", WaterPlantBlock::new, TALL_GRASS_PROPERTIES);
    public static final DeferredBlock<Block> CATTAILS = BLOCKS.registerBlock("cattails", WaterPlantBlock::new, TALL_GRASS_PROPERTIES);
    public static final DeferredBlock<Block> SMALL_LILY_PADS = BLOCKS.registerBlock("small_lily_pads", QuarteredWaterLilyBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .instabreak()
            .sound(SoundType.LILY_PAD)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> FLAMING_BROMELIAD = BLOCKS.registerBlock("flaming_bromeliad", props -> new FlamingBromeliadBlock(
        MobEffects.GLOWING, 5.0f,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY)
            .randomTicks()
            .lightLevel(state -> state.getValue(FlamingBromeliadBlock.OPEN) ? 6 : 3))
    );
    public static final DeferredBlock<Block> POTTED_FLAMING_BROMELIAD = BLOCKS.registerBlock("potted_flaming_bromeliad", props -> new FlowerPotBlock(
            () -> (FlowerPotBlock)Blocks.FLOWER_POT, FLAMING_BROMELIAD,
            BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel(state -> 9))
    );
    public static final DeferredBlock<Block> GIANT_BAMBOO_SAPLING = BLOCKS.registerBlock("giant_bamboo_sapling", GiantBambooSaplingBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .forceSolidOn()
            .randomTicks()
            .noCollission()
            .strength(1.5F)
            .sound(SoundType.BAMBOO_SAPLING)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<Block> GIANT_BAMBOO = BLOCKS.registerBlock("giant_bamboo", GiantBambooStalkBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .forceSolidOn()
            .randomTicks()
            .strength(1.5F)
            .sound(SoundType.BAMBOO)
            .noOcclusion()
            .dynamicShape()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor(NPBlocks::never));
    public static final DeferredBlock<Block> GIANT_BAMBOO_LEAVES = BLOCKS.registerBlock("giant_bamboo_leaves", GiantBambooLeavesBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(Blocks::ocelotOrParrot)
            .isSuffocating(NPBlocks::never)
            .isViewBlocking(NPBlocks::never)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor(NPBlocks::never)
    );
    public static final DeferredBlock<Block> MYCELIAL_GROWTH = BLOCKS.registerBlock("mycelial_growth", MycelialGrowthBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.ROOTS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> MYCELIAL_WEB = BLOCKS.registerBlock("mycelial_web", MycelialWebBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .replaceable()
            .noCollission()
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> BASALTIC_MINERAL_SAND = BLOCKS.registerBlock("basaltic_mineral_sand", props -> new ColoredFallingBlock(new ColorRGBA(-8356741),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.5F)
            .sound(SoundType.SAND)));
    public static final DeferredBlock<Block> PERMAFROST = BLOCKS.registerBlock("permafrost", PermafrostBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .randomTicks()
            .strength(2.8F)
            .sound(SoundType.STONE));
    public static final DeferredBlock<Block> ALFIZOL = BLOCKS.registerBlock("alfizol", AlfizolBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.GRASS));
    public static final DeferredBlock<Block> GRASSY_CLAY_HORIZON = BLOCKS.registerBlock("grassy_clay_horizon", GrassyClayHorizonBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.GRASS)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.GRASS));
    public static final DeferredBlock<Block> ROOTED_MUD = BLOCKS.registerBlock("rooted_mud", MudBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_CYAN)
            .isValidSpawn(Blocks::always)
            .isRedstoneConductor(NPBlocks::always)
            .isViewBlocking(NPBlocks::always)
            .isSuffocating(NPBlocks::always)
            .sound(SoundType.MUD)
            .strength(0.5f)
    );
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

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    public static void registerFlowerPots(FMLCommonSetupEvent event) {
        final FlowerPotBlock FLOWER_POT = ((FlowerPotBlock)Blocks.FLOWER_POT);
        FLOWER_POT.addPlant(ResourceLocation.fromNamespaceAndPath(NaturalPhilosophy.MODID, "flaming_bromeliad"), POTTED_FLAMING_BROMELIAD);
    }
}
