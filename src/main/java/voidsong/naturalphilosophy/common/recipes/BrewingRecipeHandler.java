package voidsong.naturalphilosophy.common.recipes;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.NPItems;

@EventBusSubscriber(modid = NaturalPhilosophy.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BrewingRecipeHandler {
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, NPItems.RED_ALGAE.get(), Potions.WATER_BREATHING);
    }
}
