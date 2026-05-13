package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

@SuppressWarnings("unused")
public class NPGrassColorModifiers {
    public static final EnumProxy<BiomeSpecialEffects.GrassColorModifier> LUSH_SWAMP = new EnumProxy<>(
        BiomeSpecialEffects.GrassColorModifier.class, "naturalphilosophy:lush_swamp", (BiomeSpecialEffects.GrassColorModifier.ColorModifier)
            (double x, double z, int color) -> {
                // Get the part of the color we actually care about and remove the rest
                int extracted_color = color & 16777215;
                // Get the biome noise
                double d0 = Biome.BIOME_INFO_NOISE.getValue(x * 0.0225, z * 0.0225, false);
                // Modify the color in the same way the swamp color does it
                // We move each color (R, G, B) by the fraction from the swamp "average" it was moved, then make sure it's within bounds
                int[] split = {((extracted_color >>> 16) & 255), ((extracted_color >>> 8) & 255), (extracted_color & 255)};
                if (d0 < -0.1) {
                    split = new int[]{(int)(split[0] - (split[0]*0.1648))&255, (int)(split[1] - (split[1]*0.0261))&255, (int)(split[2] - (split[2]*0.0256))&255};
                } else {
                    split = new int[]{(int)(split[0] + (split[0]*0.1648))&255, (int)(split[1] + (split[1]*0.0261))&255, (int)(split[2] + (split[2]*0.0256))&255};
                }
                return color - (color&16777215) + (split[0] << 16) + (split[1] << 8) + (split[2]);
            }
    );
}
