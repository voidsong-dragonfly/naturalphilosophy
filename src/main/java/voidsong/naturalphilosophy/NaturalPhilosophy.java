package voidsong.naturalphilosophy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static voidsong.naturalphilosophy.common.NPBlocks.BLOCKS;
import static voidsong.naturalphilosophy.common.NPItems.CREATIVE_MODE_TABS;
import static voidsong.naturalphilosophy.common.NPItems.ITEMS;
import static voidsong.naturalphilosophy.common.NPFeatures.FEATURES;

@Mod(NaturalPhilosophy.MODID)
public class NaturalPhilosophy {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "naturalphilosophy";

    public NaturalPhilosophy(IEventBus modEventBus) {
        // Register mod content
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FEATURES.register(modEventBus);
    }
}
