package voidsong.naturalphilosophy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static voidsong.naturalphilosophy.common.NPBlocks.BLOCKS;
import static voidsong.naturalphilosophy.common.NPItems.CREATIVE_MODE_TABS;
import static voidsong.naturalphilosophy.common.NPItems.ITEMS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NaturalPhilosophy.MODID)
public class NaturalPhilosophy
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "naturalphilosophy";


    /*
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path


    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build()));

    */

    public NaturalPhilosophy(IEventBus modEventBus)
    {
        // Register mod content
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

    }
}
