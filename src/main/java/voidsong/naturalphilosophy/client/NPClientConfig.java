package voidsong.naturalphilosophy.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NPClientConfig {
    // Configs
    public static ModConfigSpec.BooleanValue swingThroughVegetation;
    // Init the configs
    public static final ModConfigSpec CONFIG_SPEC;
    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        swingThroughVegetation = builder.comment("Allow weapons to swing through vegetation to hit enemies").define("swingThroughVegetation", true);
        CONFIG_SPEC = builder.build();
    }
}
