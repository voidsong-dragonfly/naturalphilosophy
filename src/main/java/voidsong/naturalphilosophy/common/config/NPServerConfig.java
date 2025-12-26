package voidsong.naturalphilosophy.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NPServerConfig {
    // Init & technical variables
    public static final ModConfigSpec CONFIG_SPEC;
    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CONFIG_SPEC = builder.build();
    }
    // Configs
    public final ModConfigSpec.IntValue maxSnowIceWaterDepth;

    NPServerConfig(ModConfigSpec.Builder builder) {
        maxSnowIceWaterDepth = builder.comment("Maximum water depth that snow will cover ice").defineInRange("maxSnowIceWaterDepth", 8, 0, 64);
    }
}
