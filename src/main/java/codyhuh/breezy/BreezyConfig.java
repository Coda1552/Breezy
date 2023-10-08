package codyhuh.breezy;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreezyConfig {
    public static boolean shouldDisplayWind;
    public static double lowWindFrequency;
    public static double mediumWindFrequency;
    public static double highWindFrequency;

    @SubscribeEvent
    public static void configLoad(ModConfigEvent.Reloading event) {
        try {
            IConfigSpec spec = event.getConfig().getSpec();
            if (spec == Client.SPEC) Client.reload();
        }
        catch (Throwable e) {
            Breezy.LOGGER.error("Something went wrong updating the breezy config, using previous or default values! {}", e.toString());
        }
    }

    public static class Client {
        public static final Client INSTANCE;
        public static final ForgeConfigSpec SPEC;

        static {
            Pair<Client, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Client::new);
            INSTANCE = pair.getLeft();
            SPEC = pair.getRight();
        }

        public final ForgeConfigSpec.BooleanValue shouldDisplayWind;
        public final ForgeConfigSpec.DoubleValue lowWindFrequency;
        public final ForgeConfigSpec.DoubleValue mediumWindFrequency;
        public final ForgeConfigSpec.DoubleValue highWindFrequency;

        Client(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            shouldDisplayWind = builder.comment("Should wind be displayed?\nDefault: true").define("should_display_wind", true);
            lowWindFrequency = builder.comment("Wind Frequency for: Forest, Taiga, and Desert biomes\nDefault: 0.005").defineInRange("low_wind_frequency", 0.005, 0.0, 1.0);
            mediumWindFrequency = builder.comment("Wind Frequency for: Plains and Savanna biomes\nDefault: 0.01").defineInRange("medium_wind_frequency", 0.01, 0.0, 1.0);
            highWindFrequency = builder.comment("Wind Frequency for: Mountainous biomes\nDefault: 0.015").defineInRange("high_wind_frequency", 0.015, 0.0, 1.0);
            builder.pop();
        }

        public static void reload() {
            BreezyConfig.shouldDisplayWind = INSTANCE.shouldDisplayWind.get();
            BreezyConfig.lowWindFrequency = INSTANCE.lowWindFrequency.get();
            BreezyConfig.mediumWindFrequency = INSTANCE.mediumWindFrequency.get();
            BreezyConfig.highWindFrequency = INSTANCE.highWindFrequency.get();
        }
    }
}