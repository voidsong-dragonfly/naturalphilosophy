package voidsong.naturalphilosophy.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NPServerConfig {
    // Configs
    public static final ModConfigSpec.IntValue maxSnowIceWaterDepth;
    // Init & technical variables
    public static final ModConfigSpec CONFIG_SPEC;
    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        maxSnowIceWaterDepth = builder.comment("Maximum water depth that snow will cover ice").defineInRange("maxSnowIceWaterDepth", 8, 0, 64);
        CONFIG_SPEC = builder.build();
    }
}
