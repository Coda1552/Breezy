package coda.breezy.common.items;

import coda.breezy.Breezy;
import coda.breezy.common.WindDirectionSavedData;
import coda.breezy.registry.BreezyItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;

public class GustGaugeItem extends Item {

    public GustGaugeItem(Properties p_41383_) {
        super(p_41383_);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Breezy.CALLBACKS.add(() -> ItemProperties.register(BreezyItems.GUST_GAUGE.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
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

                    WindDirectionSavedData data = ((ServerLevel) entity.getLevel()).getDataStorage().computeIfAbsent(WindDirectionSavedData::new, () -> new WindDirectionSavedData(entity.getLevel().getRandom()), Breezy.MOD_ID + ".savedata");

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
                        double d2 = this.getAngleTo(Vec3.atCenterOf(dir.getNormal()), entity) / (double)((float)Math.PI * 2F);
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
        })));
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
}
