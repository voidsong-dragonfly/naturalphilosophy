package voidsong.naturalphilosophy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import voidsong.naturalphilosophy.common.NPEventHandler;
import voidsong.naturalphilosophy.client.NPClientConfig;
import voidsong.naturalphilosophy.common.config.NPServerConfig;

import static voidsong.naturalphilosophy.common.NPBlocks.BLOCKS;
import static voidsong.naturalphilosophy.common.NPItems.CREATIVE_MODE_TABS;
import static voidsong.naturalphilosophy.common.NPItems.ITEMS;
import static voidsong.naturalphilosophy.common.worldgen.NPFeatures.FEATURES;
import static voidsong.naturalphilosophy.common.worldgen.NPRootPlacers.ROOT_PLACERS;
import static voidsong.naturalphilosophy.common.worldgen.NPFoliagePlacers.FOLIAGE_PLACERS;
import static voidsong.naturalphilosophy.common.worldgen.NPPlacementModifiers.PLACEMENT_MODIFIERS;

@Mod(NaturalPhilosophy.MODID)
public class NaturalPhilosophy {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "naturalphilosophy";

    public NaturalPhilosophy(ModContainer container, IEventBus modEventBus) {
        // Register mod content
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FEATURES.register(modEventBus);
        ROOT_PLACERS.register(modEventBus);
        FOLIAGE_PLACERS.register(modEventBus);
        PLACEMENT_MODIFIERS.register(modEventBus);
        // Register config handling
        container.registerConfig(ModConfig.Type.CLIENT, NPClientConfig.CONFIG_SPEC);
        container.registerConfig(ModConfig.Type.SERVER, NPServerConfig.CONFIG_SPEC);
        // Register event handlers
        NeoForge.EVENT_BUS.register(NPEventHandler.class);
    }
}
