package coda.breezy;

import coda.breezy.common.WindDirectionSavedData;
import coda.breezy.common.entities.HotAirBalloonEntity;
import coda.breezy.networking.BreezyNetworking;
import coda.breezy.networking.WindDirectionPacket;
import coda.breezy.registry.BreezyBiomeModifiers;
import coda.breezy.registry.BreezyEntities;
import coda.breezy.registry.BreezyItems;
import coda.breezy.registry.BreezyParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(Breezy.MOD_ID)
public class Breezy {
    public static final String MOD_ID = "breezy";
    public static final Logger LOGGER = LogManager.getLogger();

    public Breezy() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::commonSetup);
        bus.addListener(this::registerEntityAttributes);

        forgeBus.addListener(this::resetWindDirection);
        forgeBus.addListener(this::syncWindDataOnJoinWorld);

        BreezyParticles.PARTICLES.register(bus);
        BreezyEntities.ENTITIES.register(bus);
        BreezyItems.ITEMS.register(bus);
        BreezyBiomeModifiers.BIOME_MODIFIERS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BreezyConfig.Client.SPEC);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent e) {
        e.put(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonEntity.createAttributes().build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BreezyNetworking.register();
    }

    private void resetWindDirection(TickEvent.LevelTickEvent e) {
        Level world = e.level;

        if (!world.isClientSide && world.getDayTime() % 24000 == 0) {
            WindDirectionSavedData.resetWindDirection(new Random());

            world.players().forEach(player -> {
                Level level = player.getLevel();

                if (level.isClientSide) return;

                WindDirectionSavedData data = ((ServerLevel) level).getDataStorage().computeIfAbsent(WindDirectionSavedData::new, () -> new WindDirectionSavedData(level.getRandom()), Breezy.MOD_ID + ".savedata");
                BreezyNetworking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
            });
        }
    }

    public void syncWindDataOnJoinWorld(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof Player player && !e.getLevel().isClientSide) {
            WindDirectionSavedData data = ((ServerLevel) player.getLevel()).getDataStorage().computeIfAbsent(WindDirectionSavedData::new, () -> new WindDirectionSavedData(e.getLevel().getRandom()), Breezy.MOD_ID + ".savedata");
            BreezyNetworking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
        }
    }
}