package codyhuh.breezy.client;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.client.render.HotAirBalloonRenderer;
import codyhuh.breezy.common.network.NewWindSavedData;
import codyhuh.breezy.common.network.BreezyNetworking;
import codyhuh.breezy.core.other.util.WindMathUtil;
import codyhuh.breezy.core.registry.BreezyEntities;
import codyhuh.breezy.core.registry.BreezyItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonRenderer::new);
    }

    @SubscribeEvent
    public static void registerClient(final FMLClientSetupEvent event) {
        ItemProperties.register(BreezyItems.GUST_GAUGE.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
            private final GustGaugeWobble gaugeWobble = new GustGaugeWobble();
            private final GustGaugeWobble randomWobble = new GustGaugeWobble();

            public float unclampedCall(@NotNull ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
                Entity entity = livingEntity != null ? livingEntity : stack.getEntityRepresentation();
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (clientLevel == null && entity.level() instanceof ClientLevel) {
                        clientLevel = (ClientLevel)entity.level();
                    }

                    NewWindSavedData windData = BreezyNetworking.CLIENT_CACHE;

                    long gameTime = clientLevel != null ? clientLevel.getGameTime() : 0;
                    if (windData != null) {
                        double windDirection = windData.getWindAtHeight(entity.blockPosition().getY(), entity.level());
                        boolean isLocal = livingEntity instanceof Player player && player.isLocalPlayer();
                        double entityRot = 0.0D;
                        if (isLocal) {
                            entityRot = livingEntity.getYRot();
                        } else if (entity instanceof ItemFrame) {
                            entityRot = this.getFrameRotation((ItemFrame)entity);
                        } else if (entity instanceof ItemEntity) {
                            entityRot = 180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F;
                        } else if (livingEntity != null) {
                            entityRot = livingEntity.yBodyRot;
                        }

                        entityRot = Mth.positiveModulo(entityRot / 360.0D, 1.0D);
                        double windAngle = this.getAngleTo(new Vec3(WindMathUtil.stepX(windDirection),
                                0.0, WindMathUtil.stepZ(windDirection)).scale(Double.MAX_VALUE), entity) / ((float)Math.PI * 2F);
                        double finalAngle;
                        if (isLocal) {
                            if (this.gaugeWobble.shouldUpdate(gameTime)) {
                                this.gaugeWobble.update(gameTime, 0.5D - (entityRot - 0.25D));
                            }

                            finalAngle = windAngle + this.gaugeWobble.rotation;
                        } else {
                            finalAngle = 0.5D - (entityRot - 0.25D - windAngle);
                        }

                        return Mth.positiveModulo((float)finalAngle, 1.0F);
                    } else {
                        if (this.randomWobble.shouldUpdate(gameTime)) {
                            this.randomWobble.update(gameTime, Math.random());
                        }

                        double randomAngle = this.randomWobble.rotation + (double)((float)this.hash(seed) / 2.14748365E9F);
                        return Mth.positiveModulo((float)randomAngle, 1.0F);
                    }
                }
                // This method returns a value from 0.0 to 1.0, where 0 and 1 result in the gust gauge pointing straight up
            }

            private int hash(int seed) {
                return seed * 1327217883;
            }

            private double getFrameRotation(ItemFrame itemFrame) {
                Direction direction = itemFrame.getDirection();
                int rotationOffset = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + rotationOffset);
            }

            private double getAngleTo(Vec3 targetPosition, Entity entity) {
                return Math.atan2(targetPosition.z() - entity.getZ(), targetPosition.x() - entity.getX());
            }
        });
    }

    private static class GustGaugeWobble {
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        boolean shouldUpdate(long currentTick) {
            return this.lastUpdateTick != currentTick;
        }

        void update(long currentTick, double targetRotation) {
            this.lastUpdateTick = currentTick;
            double rotationDifference = targetRotation - this.rotation;
            rotationDifference = Mth.positiveModulo(rotationDifference + 0.5D, 1.0D) - 0.5D;
            this.deltaRotation += rotationDifference * 0.1D;
            this.deltaRotation *= 0.8D;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
        }
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register((stack, color) -> color > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack), BreezyItems.HOT_AIR_BALLOON.get());
    }
}
