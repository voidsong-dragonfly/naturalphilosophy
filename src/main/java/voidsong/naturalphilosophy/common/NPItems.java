package voidsong.naturalphilosophy.common;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;

public class NPItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NaturalPhilosophy.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NaturalPhilosophy.MODID);

    public static final DeferredItem<BlockItem> DUNE_GRASS = ITEMS.registerSimpleBlockItem("dune_grass", NPBlocks.DUNE_GRASS);
    public static final DeferredItem<BlockItem> RUSHES = ITEMS.registerSimpleBlockItem("rushes", NPBlocks.RUSHES);
    public static final DeferredItem<BlockItem> CATTAILS = ITEMS.registerSimpleBlockItem("cattails", NPBlocks.CATTAILS);
    public static final DeferredItem<BlockItem> LARGE_BUSH = ITEMS.registerSimpleBlockItem("large_bush", NPBlocks.LARGE_BUSH);
    public static final DeferredItem<BlockItem> GIANT_BAMBOO_SAPLING = ITEMS.registerSimpleBlockItem("giant_bamboo_sapling", NPBlocks.GIANT_BAMBOO_SAPLING);
    public static final DeferredItem<BlockItem> GIANT_BAMBOO_LEAVES = ITEMS.registerSimpleBlockItem("giant_bamboo_leaves", NPBlocks.GIANT_BAMBOO_LEAVES);
    public static final DeferredItem<Item> GIANT_BAMBOO = ITEMS.registerSimpleItem("giant_bamboo");
    public static final DeferredItem<BlockItem> MYCELIAL_GROWTH = ITEMS.registerSimpleBlockItem("mycelial_growth", NPBlocks.MYCELIAL_GROWTH);
    public static final DeferredItem<BlockItem> MYCELIAL_WEB = ITEMS.registerSimpleBlockItem("mycelial_web", NPBlocks.MYCELIAL_WEB);
    public static final DeferredItem<BlockItem> BASALTIC_MINERAL_SAND = ITEMS.registerSimpleBlockItem("basaltic_mineral_sand", NPBlocks.BASALTIC_MINERAL_SAND);
    public static final DeferredItem<BlockItem> PERMAFROST = ITEMS.registerSimpleBlockItem("permafrost", NPBlocks.PERMAFROST);
    public static final DeferredItem<BlockItem> ALFIZOL = ITEMS.registerSimpleBlockItem("alfizol", NPBlocks.ALFIZOL);
    public static final DeferredItem<BlockItem> GRASS_CLAY_HORIZON = ITEMS.registerSimpleBlockItem("grassy_clay_horizon", NPBlocks.GRASSY_CLAY_HORIZON);
    public static final DeferredItem<BlockItem> ROOTED_MUD = ITEMS.registerSimpleBlockItem("rooted_mud", NPBlocks.ROOTED_MUD);
    public static final DeferredItem<BlockItem> RED_ALGAE = ITEMS.registerSimpleBlockItem("red_algae", NPBlocks.RED_ALGAE);
    public static final DeferredItem<BlockItem> BONE_CORAL = ITEMS.registerSimpleBlockItem("bone_coral", NPBlocks.BONE_CORAL);
    public static final DeferredItem<BlockItem> BONE_CORAL_FAN = ITEMS.register("bone_coral_fan", rl ->
        new StandingAndWallBlockItem(NPBlocks.BONE_CORAL_FAN.get(), NPBlocks.BONE_CORAL_WALL_FAN.get(), new Item.Properties(), Direction.DOWN)
    );
    public static final DeferredItem<BlockItem> DEAD_BONE_CORAL = ITEMS.registerSimpleBlockItem("dead_bone_coral", NPBlocks.DEAD_BONE_CORAL);
    public static final DeferredItem<BlockItem> DEAD_BONE_CORAL_FAN = ITEMS.register("dead_bone_coral_fan", rl ->
        new StandingAndWallBlockItem(NPBlocks.DEAD_BONE_CORAL_FAN.get(), NPBlocks.DEAD_BONE_CORAL_WALL_FAN.get(), new Item.Properties(), Direction.DOWN)
    );

    // Creates a creative tab for the mod & adds all Natural Philosophy items to the tab
    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("naturalphilosophy_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.naturalphilosophy"))
        .icon(() -> DUNE_GRASS.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(DUNE_GRASS);
            output.accept(RUSHES);
            output.accept(CATTAILS);
            output.accept(LARGE_BUSH);
            output.accept(GIANT_BAMBOO_SAPLING);
            output.accept(GIANT_BAMBOO_LEAVES);
            output.accept(GIANT_BAMBOO);
            output.accept(MYCELIAL_GROWTH);
            output.accept(MYCELIAL_WEB);
            output.accept(BASALTIC_MINERAL_SAND);
            output.accept(PERMAFROST);
            output.accept(ALFIZOL);
            output.accept(GRASS_CLAY_HORIZON);
            output.accept(ROOTED_MUD);
            output.accept(RED_ALGAE);
            output.accept(BONE_CORAL);
            output.accept(BONE_CORAL_FAN);
            output.accept(DEAD_BONE_CORAL);
            output.accept(DEAD_BONE_CORAL_FAN);
        }).build());
}
