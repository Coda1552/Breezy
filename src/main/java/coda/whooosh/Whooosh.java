package coda.whooosh;

import coda.whooosh.common.HotAirBalloonEntity;
import coda.whooosh.registry.WhoooshEntities;
import coda.whooosh.registry.WhoooshParticles;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Whooosh.MOD_ID)
public class Whooosh {
    public static final String MOD_ID = "whooosh";
    public static final Logger LOGGER = LogManager.getLogger();

    public Whooosh() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::registerEntityAttributes);

        forgeBus.addListener(this::addWindParticles);
        forgeBus.register(this);

        WhoooshParticles.PARTICLES.register(bus);
        WhoooshEntities.ENTITIES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WhoooshConfig.Client.SPEC);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(WhoooshEntities.HOT_AIR_BALLOON.get(), HotAirBalloonEntity.createAttributes().build());
    }


    private void addWindParticles(BiomeLoadingEvent event) {
        BiomeSpecialEffects baseEffects = event.getEffects();

        BiomeSpecialEffects defaultEffects = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(WhoooshParticles.WIND.get(), 0.0002F)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects lowWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(WhoooshParticles.WIND.get(), WhoooshConfig.Client.INSTANCE.lowWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects mediumWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(WhoooshParticles.WIND.get(), WhoooshConfig.Client.INSTANCE.mediumWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects highWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(WhoooshParticles.WIND.get(), WhoooshConfig.Client.INSTANCE.highWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();

        Biome.BiomeCategory category = event.getCategory();

        if (WhoooshConfig.Client.INSTANCE.shouldDisplayWind.get()) {
            switch (category) {
                // LOW
                case FOREST: event.setEffects(lowWind);
                case TAIGA: event.setEffects(lowWind);
                case DESERT: event.setEffects(lowWind);
                // MEDIUM
                case PLAINS: event.setEffects(mediumWind);
                case SAVANNA: event.setEffects(mediumWind);
                // HIGH
                case MOUNTAIN: event.setEffects(highWind);
                case EXTREME_HILLS: event.setEffects(highWind);
                case ICY: event.setEffects(highWind);

                default: event.setEffects(defaultEffects);
            }
        }
    }
}