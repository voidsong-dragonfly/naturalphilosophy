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

    public static final DeferredItem<BlockItem> DUNE_GRASS_ITEM = ITEMS.registerSimpleBlockItem("dune_grass", NPBlocks.DUNE_GRASS);
    public static final DeferredItem<BlockItem> RUSHES_ITEM = ITEMS.registerSimpleBlockItem("rushes", NPBlocks.RUSHES);
    public static final DeferredItem<BlockItem> CATTAILS_ITEM = ITEMS.registerSimpleBlockItem("cattails", NPBlocks.CATTAILS);
    public static final DeferredItem<BlockItem> MYCELIAL_GROWTH_ITEM = ITEMS.registerSimpleBlockItem("mycelial_growth", NPBlocks.MYCELIAL_GROWTH);
    public static final DeferredItem<BlockItem> RED_ALGAE_ITEM = ITEMS.registerSimpleBlockItem("red_algae", NPBlocks.RED_ALGAE);
    public static final DeferredItem<BlockItem> BONE_CORAL_ITEM = ITEMS.registerSimpleBlockItem("bone_coral", NPBlocks.BONE_CORAL);
    public static final DeferredItem<BlockItem> BONE_CORAL_FAN_ITEM = ITEMS.register("bone_coral_fan", rl ->
        new StandingAndWallBlockItem(NPBlocks.BONE_CORAL_FAN.get(), NPBlocks.BONE_CORAL_WALL_FAN.get(), new Item.Properties(), Direction.DOWN)
    );
    public static final DeferredItem<BlockItem> DEAD_BONE_CORAL_ITEM = ITEMS.registerSimpleBlockItem("dead_bone_coral", NPBlocks.DEAD_BONE_CORAL);
    public static final DeferredItem<BlockItem> DEAD_BONE_CORAL_FAN_ITEM = ITEMS.register("dead_bone_coral_fan", rl ->
        new StandingAndWallBlockItem(NPBlocks.DEAD_BONE_CORAL_FAN.get(), NPBlocks.DEAD_BONE_CORAL_WALL_FAN.get(), new Item.Properties(), Direction.DOWN)
    );
    public static final DeferredItem<BlockItem> BASALTIC_MINERAL_SAND_ITEM = ITEMS.registerSimpleBlockItem("basaltic_mineral_sand", NPBlocks.BASALTIC_MINERAL_SAND);
    public static final DeferredItem<BlockItem> SANDY_MYCELIUM_ITEM = ITEMS.registerSimpleBlockItem("sandy_mycelium", NPBlocks.SANDY_MYCELIUM);
    public static final DeferredItem<BlockItem> PERMAFROST_ITEM = ITEMS.registerSimpleBlockItem("permafrost", NPBlocks.PERMAFROST);
    public static final DeferredItem<BlockItem> ALFIZOL_ITEM = ITEMS.registerSimpleBlockItem("alfizol", NPBlocks.ALFIZOL);

    // Creates a creative tab for the mod & adds all Natural Philosophy items to the tab
    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("naturalphilosophy_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.naturalphilosophy"))
        .icon(() -> DUNE_GRASS_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(DUNE_GRASS_ITEM);
            output.accept(RUSHES_ITEM);
            output.accept(CATTAILS_ITEM);
            output.accept(MYCELIAL_GROWTH_ITEM);
            output.accept(RED_ALGAE_ITEM);
            output.accept(BONE_CORAL_ITEM);
            output.accept(BONE_CORAL_FAN_ITEM);
            output.accept(DEAD_BONE_CORAL_ITEM);
            output.accept(DEAD_BONE_CORAL_FAN_ITEM);
            output.accept(BASALTIC_MINERAL_SAND_ITEM);
            output.accept(SANDY_MYCELIUM_ITEM);
            output.accept(PERMAFROST_ITEM);
            output.accept(ALFIZOL_ITEM);
        }).build());
}
