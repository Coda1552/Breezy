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

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Breezy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonRenderer::new);
    }

    @SubscribeEvent
    public static void registerClient(final FMLClientSetupEvent e) {
        ItemProperties.register(BreezyItems.GUST_GAUGE.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
            private final CompassWobble wobble = new CompassWobble();
            private final CompassWobble wobbleRandom = new CompassWobble();

            public float unclampedCall(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int p_174675_) {
                Entity entity = livingEntity != null ? livingEntity : stack.getEntityRepresentation();
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (clientLevel == null && entity.level() instanceof ClientLevel) {
                        clientLevel = (ClientLevel)entity.level();
                    }

                    NewWindSavedData data = BreezyNetworking.CLIENT_CACHE;

                    long i = clientLevel != null ? clientLevel.getGameTime() : 0;
                    if (data != null) {
                        double direction = data.getWindAtHeight(entity.blockPosition().getY(), entity.level());
                        boolean flag = livingEntity instanceof Player player && player.isLocalPlayer();
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
                        double d2 = this.getAngleTo(new Vec3(WindMathUtil.stepX(direction),
                                0.0, WindMathUtil.stepZ(direction)).scale(Double.MAX_VALUE), entity) / ((float)Math.PI * 2F);
                        double d3;
                        if (flag) {
                            if (this.wobble.shouldUpdate(i)) {
                                this.wobble.update(i, 0.5D - (d1 - 0.25D));
                            }

                            d3 = d2 + this.wobble.rotation;
                        } else {
                            d3 = 0.5D - (d1 - 0.25D - d2);
                        }

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

    private static class CompassWobble {
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

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register((stack, color) -> color > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack), BreezyItems.HOT_AIR_BALLOON.get());
    }
}
