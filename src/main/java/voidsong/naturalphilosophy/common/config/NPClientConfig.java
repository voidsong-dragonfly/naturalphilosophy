package voidsong.naturalphilosophy.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NPClientConfig {
    // Init & technical variables
    public static final ModConfigSpec CONFIG_SPEC;
    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CONFIG_SPEC = builder.build();
    }
    // Configs
    public final ModConfigSpec.BooleanValue swingThroughVegetation;

    NPClientConfig(ModConfigSpec.Builder builder) {
        swingThroughVegetation = builder.comment("Allow weapons to swing through vegetation to hit enemies").define("swingThroughVegetation", true);
    }
}
