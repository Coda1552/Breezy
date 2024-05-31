package codyhuh.breezy;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreezyConfig {
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Integer> windPeriodLength;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("windpatterns");
            windPeriodLength = builder.comment("Duration in ticks for how often the wind changes\nDefault: 24000").define("wind_period_length", 24000);
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> shouldDisplayWind;
        public final ForgeConfigSpec.ConfigValue<Double> lowWindFrequency;
        public final ForgeConfigSpec.ConfigValue<Double> defaultWindFrequency;
        public final ForgeConfigSpec.ConfigValue<Double> highWindFrequency;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("particles");
            shouldDisplayWind = builder.comment("Should wind be displayed?\nDefault: true").define("should_display_wind", true);
            lowWindFrequency = builder.comment("Wind Frequency for: Forests, Taigas, and Deserts\nDefault: 0.005").defineInRange("low_wind_frequency", 0.005, 0.0, 1.0);
            defaultWindFrequency = builder.comment("Wind Frequency for: Plains and Savanna biomes\nDefault: 0.01").defineInRange("medium_wind_frequency", 0.01, 0.0, 1.0);
            highWindFrequency = builder.comment("Wind Frequency for: Mountainous biomes\nDefault: 0.015").defineInRange("high_wind_frequency", 0.015, 0.0, 1.0);
            builder.pop();
        }
    }

    static final ForgeConfigSpec COMMON_SPEC;
    public static final BreezyConfig.Common COMMON;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(BreezyConfig.Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }
}