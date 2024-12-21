package voidsong.naturalphilosophy.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.NPBlocks;

@Mod.EventBusSubscriber(modid = NaturalPhilosophy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColorHandler {

    @SubscribeEvent
    public static void handleGrassColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) ->
                world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D),
            NPBlocks.DUNE_GRASS.get(), NPBlocks.TALL_DUNE_GRASS.get());
    }
}
