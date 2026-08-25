package voidsong.naturalphilosophy.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.NPBlocks;
import voidsong.naturalphilosophy.common.NPItems;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = NaturalPhilosophy.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ColorHandler {

    @SubscribeEvent
    public static void handleGrassColors(RegisterColorHandlersEvent.Block event) {
        // Natural Philosophy blocks to register, such as Dune Grass
        event.register((state, world, pos, tintIndex) ->
                world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D),
            NPBlocks.DUNE_GRASS.get(), NPBlocks.TALL_DUNE_GRASS.get(), NPBlocks.GRASSY_CLAY_HORIZON.get(),
            NPBlocks.CATTAILS.get(), NPBlocks.RUSHES.get());
        event.register((state, world, pos, tintIndex) ->
                world != null && pos != null ? -14647248 : -9321636, NPBlocks.SMALL_LILY_PADS.get());
    }

    @SubscribeEvent
    public static void handleGrassColors(RegisterColorHandlersEvent.Item event) {
        // Natural Philosophy blocks to register, such as Dune Grass
        event.register((stack, tintIndex) ->
            tintIndex == 0 ? GrassColor.get(0.5D, 1.0D) : 0xFFFFFFFF,
            NPItems.DUNE_GRASS, NPItems.GRASS_CLAY_HORIZON, NPItems.RUSHES, NPItems.CATTAILS);
        event.register((stack, tintIndex) ->
                event.getBlockColors().getColor(((BlockItem)stack.getItem()).getBlock().defaultBlockState(), null, null, tintIndex),
                NPItems.SMALL_LILY_PADS.get()
        );
    }
}
