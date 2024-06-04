package codyhuh.breezy;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreezyConfig {
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Integer> windPeriodLength;
        public final ForgeConfigSpec.ConfigValue<Double> changePercentage;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("windpatterns");
            windPeriodLength = builder.comment("Duration in ticks for how often the wind changes\nDefault: 24000").define("wind_period_length", 24000);
            changePercentage = builder.comment("Percent by which wind changes each duration\nDefault: 2.5%").defineInRange("wind_change_percent", 0.025, 0.0, 1.0);
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> shouldDisplayWind;
        public final ForgeConfigSpec.ConfigValue<Integer> minimumWindHeight;
        public final ForgeConfigSpec.ConfigValue<Boolean> windMustSeeSky;
        public final ForgeConfigSpec.ConfigValue<Double> lowWindFrequency;
        public final ForgeConfigSpec.ConfigValue<Double> defaultWindFrequency;
        public final ForgeConfigSpec.ConfigValue<Double> highWindFrequency;
        public final ForgeConfigSpec.ConfigValue<Boolean> balloonsAlwaysRender;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("client");
            builder.push("particles");
            shouldDisplayWind = builder.comment("Should wind be displayed?\nDefault: true").define("should_display_wind", true);
            minimumWindHeight = builder.comment("How high above sea level should wind be displayed?\nDefault: 20").define("minimum_wind_height_above_sea_level", 20);
            windMustSeeSky = builder.comment("Wind particles may only spawn in blocks with access to sky\nDefault: true").define("wind_must_see_sky", true);
            builder.push("biome dependent frequency multipliers");
            lowWindFrequency = builder.comment("Wind freq multiplier for low_wind biomes\nDefault: 0.005").defineInRange("low_wind_frequency", 0.005, 0.0, 1.0);
            defaultWindFrequency = builder.comment("Wind freq multiplier by default\nDefault: 0.01").defineInRange("medium_wind_frequency", 0.01, 0.0, 1.0);
            highWindFrequency = builder.comment("Wind freq multiplier for high_wind biomes\nDefault: 0.015").defineInRange("high_wind_frequency", 0.015, 0.0, 1.0);
            builder.pop();
            builder.pop();
            balloonsAlwaysRender = builder.comment("Should balloons render from farther distances?\nDefault: true").define("balloons_render_farther_away", true);
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