package coda.whooosh;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Whooosh.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WhoooshConfig {
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
            Whooosh.LOGGER.error("Something went wrong updating the Whooosh config, using previous or default values! {}", e.toString());
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
            shouldDisplayWind = builder.comment("Should wind be displayed?").define("should_display_wind", true);
            lowWindFrequency = builder.comment("Wind Frequency for: Forest, Taiga, and Desert biomes").defineInRange("low_wind_frequency", 0.00025, 0.0, 0.05);
            mediumWindFrequency = builder.comment("Wind Frequency for: Plains and Savanna biomes").defineInRange("medium_wind_frequency", 0.0005, 0.0, 0.05);
            highWindFrequency = builder.comment("Wind Frequency for: Icy, Extreme Hills, and Mountain biomes").defineInRange("high_wind_frequency", 0.00075, 0.0, 0.05);
            builder.pop();
        }

        public static void reload() {
            WhoooshConfig.shouldDisplayWind = INSTANCE.shouldDisplayWind.get();
            WhoooshConfig.lowWindFrequency = INSTANCE.lowWindFrequency.get();
            WhoooshConfig.mediumWindFrequency = INSTANCE.mediumWindFrequency.get();
            WhoooshConfig.highWindFrequency = INSTANCE.highWindFrequency.get();
        }
    }
}