package voidsong.naturalphilosophy.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;

public class NPItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NaturalPhilosophy.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NaturalPhilosophy.MODID);

    public static final DeferredItem<BlockItem> DUNE_GRASS_ITEM = ITEMS.registerSimpleBlockItem("dune_grass", NPBlocks.DUNE_GRASS);
    public static final DeferredItem<BlockItem> RUSHES_ITEM = ITEMS.registerSimpleBlockItem("rushes", NPBlocks.RUSHES);

    // Creates a creative tab for the mod & adds all Natural Philosophy items to the tab
    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("naturalphilosophy_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.naturalphilosophy"))
        .icon(() -> DUNE_GRASS_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(DUNE_GRASS_ITEM.get());
            output.accept(RUSHES_ITEM.get());
        }).build());
}
