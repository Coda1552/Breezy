package coda.wind;

import coda.wind.init.WindParticles;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Wind.MOD_ID)
public class Wind {
    public static final String MOD_ID = "wind";

    public Wind() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        forgeBus.addListener(this::addWindParticles);

        WindParticles.PARTICLES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addWindParticles(BiomeLoadingEvent event) {
        BiomeSpecialEffects baseEffects = event.getEffects();
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(WindParticles.WIND.get(), 0.001F)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();

        event.setEffects(effects);

    }
}