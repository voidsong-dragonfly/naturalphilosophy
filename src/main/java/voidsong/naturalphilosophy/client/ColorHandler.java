package voidsong.naturalphilosophy.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
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
        // Vanilla blocks we add color to, such as bushes
        event.register((state, world, pos, tintIndex) ->
                world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor(),
            Blocks.PEONY, Blocks.ROSE_BUSH, Blocks.LILAC, Blocks.SUNFLOWER);
    }

    @SubscribeEvent
    public static void handleGrassColors(RegisterColorHandlersEvent.Item event) {
        // Natural Philosophy blocks to register, such as Dune Grass
        event.register((stack, tintIndex) ->
            tintIndex == 0 ? GrassColor.get(0.5D, 1.0D) : 0xFFFFFFFF,
            NPItems.DUNE_GRASS, NPItems.GRASS_CLAY_HORIZON,
            NPItems.RUSHES, NPItems.CATTAILS);
        // Vanilla blocks we add color to, such as bushes
        event.register((stack, tintIndex) ->
                tintIndex == 1 ? FoliageColor.getDefaultColor() : 0xFFFFFFFF,
            Items.PEONY, Items.ROSE_BUSH, Items.LILAC);
    }
}
