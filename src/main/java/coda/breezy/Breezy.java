package coda.breezy;

import coda.breezy.common.WindDirectionSavedData;
import coda.breezy.common.entities.HotAirBalloonEntity;
import coda.breezy.networking.BreezyNetowrking;
import coda.breezy.networking.WindDirectionPacket;
import coda.breezy.registry.BreezyEntities;
import coda.breezy.registry.BreezyItems;
import coda.breezy.registry.BreezyParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// todo- add gust gauge
@Mod(Breezy.MOD_ID)
public class Breezy {
    public static final String MOD_ID = "breezy";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public Breezy() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::registerEntityAttributes);
        bus.addListener(this::registerClient);
        bus.addListener(this::commonSetup);

        forgeBus.addListener(this::addWindParticles);
        forgeBus.addListener(this::resetWindDirection);
        forgeBus.addListener(this::syncWindDataOnJoinWorld);

        BreezyParticles.PARTICLES.register(bus);
        BreezyEntities.ENTITIES.register(bus);
        BreezyItems.ITEMS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BreezyConfig.Client.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BreezyNetowrking.register();
    }

    private void registerClient(FMLClientSetupEvent event) {
        ItemProperties.register(BreezyItems.GUST_GAUGE.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
            private final CompassWobble wobble = new CompassWobble();
            private final CompassWobble wobbleRandom = new CompassWobble();

            public float unclampedCall(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int p_174675_) {
                Entity entity = livingEntity != null ? livingEntity : stack.getEntityRepresentation();
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (clientLevel == null && entity.level instanceof ClientLevel) {
                        clientLevel = (ClientLevel)entity.level;
                    }

                    WindDirectionSavedData data = BreezyNetowrking.CLIENT_CACHE;

                    Direction dir = data.getWindDirection(entity.blockPosition().getY(), entity.getLevel());

                    long i = clientLevel.getGameTime();
                    if (dir != null) {
                        boolean flag = livingEntity instanceof Player && ((Player)livingEntity).isLocalPlayer();
                        double d1 = 0.0D;
                        if (flag) {
                            d1 = livingEntity.getYRot();
                        } else if (entity instanceof ItemFrame) {
                            d1 = this.getFrameRotation((ItemFrame)entity);
                        } else if (entity instanceof ItemEntity) {
                            d1 = 180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F;
                        } else if (livingEntity != null) {
                            d1 = livingEntity.yBodyRot;
                        }

                        d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
                        Vec3i norm = dir.getNormal();
                        double d2 = this.getAngleTo(new Vec3(norm.getX() * 100, norm.getY() * 100, norm.getZ() * 100), entity) / ((float)Math.PI * 2F);
                        double d3;
                        if (flag) {
                            if (this.wobble.shouldUpdate(i)) {
                                this.wobble.update(i, 0.5D - (d1 - 0.25D));
                            }

                            d3 = d2 + this.wobble.rotation;
                        } else {
                            d3 = 0.5D - (d1 - 0.25D - d2);
                        }
    
                        System.out.println(dir);
                        
                        return Mth.positiveModulo((float)d3, 1.0F);
                    } else {
                        if (this.wobbleRandom.shouldUpdate(i)) {
                            this.wobbleRandom.update(i, Math.random());
                        }

                        double d0 = this.wobbleRandom.rotation + (double)((float)this.hash(p_174675_) / 2.14748365E9F);
                        return Mth.positiveModulo((float)d0, 1.0F);
                    }
                }
            }

            private int hash(int p_174670_) {
                return p_174670_ * 1327217883;
            }

            private double getFrameRotation(ItemFrame p_117914_) {
                Direction direction = p_117914_.getDirection();
                int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + p_117914_.getRotation() * 45 + i);
            }

            private double getAngleTo(Vec3 p_117919_, Entity p_117920_) {
                return Math.atan2(p_117919_.z() - p_117920_.getZ(), p_117919_.x() - p_117920_.getX());
            }
        });
    }

    static class CompassWobble {
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        boolean shouldUpdate(long p_117934_) {
            return this.lastUpdateTick != p_117934_;
        }

        void update(long p_117936_, double p_117937_) {
            this.lastUpdateTick = p_117936_;
            double d0 = p_117937_ - this.rotation;
            d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.deltaRotation += d0 * 0.1D;
            this.deltaRotation *= 0.8D;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent e) {
        e.put(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonEntity.createAttributes().build());
    }

    private void resetWindDirection(TickEvent.WorldTickEvent e) {
        Level world = e.world;

        if (world.getDayTime() % 24000 == 0) {
            WindDirectionSavedData.resetWindDirection(world.random);
            
            world.players().forEach(player -> {
                WindDirectionSavedData data = ((ServerLevel) player.getLevel()).getDataStorage().computeIfAbsent(WindDirectionSavedData::new, () -> new WindDirectionSavedData(player.getLevel().getRandom()), Breezy.MOD_ID + ".savedata");
                BreezyNetowrking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
            });
        }
    }
    
    public void syncWindDataOnJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!player.level.isClientSide) {
                WindDirectionSavedData data = ((ServerLevel) player.getLevel()).getDataStorage().computeIfAbsent(WindDirectionSavedData::new, () -> new WindDirectionSavedData(player.getLevel().getRandom()), Breezy.MOD_ID + ".savedata");
                BreezyNetowrking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
            }
        }
    }
    
    private void addWindParticles(BiomeLoadingEvent e) {
        BiomeSpecialEffects baseEffects = e.getEffects();

        BiomeSpecialEffects defaultEffects = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), 0.0002F)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects lowWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.lowWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects mediumWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.mediumWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects highWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.highWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();

        Biome.BiomeCategory category = e.getCategory();

        if (category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
            return;
        }
        if (BreezyConfig.Client.INSTANCE.shouldDisplayWind.get()) {
            switch (category) {
                // LOW
                case FOREST: e.setEffects(lowWind);
                case TAIGA: e.setEffects(lowWind);
                case DESERT: e.setEffects(lowWind);
                // MEDIUM
                case PLAINS: e.setEffects(mediumWind);
                case SAVANNA: e.setEffects(mediumWind);
                // HIGH
                case MOUNTAIN: e.setEffects(highWind);
                case EXTREME_HILLS: e.setEffects(highWind);
                case ICY: e.setEffects(highWind);

                default: e.setEffects(defaultEffects);
            }
        }
    }
}