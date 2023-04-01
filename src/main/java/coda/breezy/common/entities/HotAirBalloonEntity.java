package coda.breezy.common.entities;

import coda.breezy.common.WindDirectionSavedData;
import coda.breezy.networking.BreezyNetworking;
import coda.breezy.registry.BreezyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class HotAirBalloonEntity extends Entity {
    private static final EntityDataAccessor<Integer> LITNESS = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SANDBAGS = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);

    public HotAirBalloonEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    protected void defineSynchedData() {
        this.entityData.define(LITNESS, 0);
        this.entityData.define(SANDBAGS, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (getLitness() > 0 && tickCount % (getLitness() * 40) == 0 && random.nextBoolean()) {
            setLitness(getLitness() - 1);
        }

        if (getLitness() > 0 && !isVehicle()) {
            setLitness(0);
        }

        if (isInWaterOrRain()) {
            setLitness(0);
        }

        // Flame particle
        /*
        if (tickCount % 10 == 0 && getLitness() > 0) {
            for (int i = 0; i < getLitness(); i++) {
                level.addParticle(ParticleTypes.FLAME, getX(), getY() + 2.35D,  getZ(), getDeltaMovement().x, 0.1D, getDeltaMovement().z);
            }
        }*/
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (player.getItemInHand(hand).isEmpty()) {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            else {
                return InteractionResult.SUCCESS;
            }
        }

        if (hasPassenger(this)) {
            if (getLitness() < 5 && isVehicle() && getControllingPassenger().is(player)) {
                if (player.getItemInHand(hand).is(BreezyTags.IGNITION_SOURCES)) {
                    setLitness(getLitness() + 1);
                    playSound(SoundEvents.CAMPFIRE_CRACKLE, 1.0F, 1.0F);
                    player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    return InteractionResult.SUCCESS;
                }
            }

            if (getSandbags() < 8 && player.getItemInHand(hand).is(ItemTags.SAND)) {
                setSandbags(getSandbags() + 1);
                playSound(SoundEvents.SAND_PLACE, 1.0F, 1.0F);

                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }

                return InteractionResult.SUCCESS;
            }

            if (getSandbags() > 0 && player.getItemInHand(hand).is(Tags.Items.SHEARS)) {
                setSandbags(getSandbags() - 1);
                playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).isEmpty()) {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            else {
                return InteractionResult.SUCCESS;
            }
        }

        if (hasPassenger(this)) {
            if (getLitness() < 5 && isVehicle() && getControllingPassenger().is(player)) {
                if (player.getItemInHand(hand).is(BreezyTags.IGNITION_SOURCES)) {
                    setLitness(getLitness() + 1);
                    playSound(SoundEvents.CAMPFIRE_CRACKLE, 1.0F, 1.0F);
                    player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    return InteractionResult.SUCCESS;
                }
            }

            if (getSandbags() < 8 && player.getItemInHand(hand).is(ItemTags.SAND)) {
                setSandbags(getSandbags() + 1);
                playSound(SoundEvents.SAND_PLACE, 1.0F, 1.0F);

                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }

                return InteractionResult.SUCCESS;
            }

            if (getSandbags() > 0 && player.getItemInHand(hand).is(Tags.Items.SHEARS)) {
                setSandbags(getSandbags() - 1);
                playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }


    // todo - make this code run when the balloon is broken

/*        if (player.isShiftKeyDown()) {
            discard();
            spawnAtLocation(new ItemStack(BreezyItems.HOT_AIR_BALLOON.get()));
            playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }*/

    @Override
    public boolean mayInteract(Level p_146843_, BlockPos p_146844_) {
        return true;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Nullable
    private Vec3 getDismountLocationInDirection(Vec3 p_30562_, LivingEntity p_30563_) {
        double d0 = this.getX() + p_30562_.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.getZ() + p_30562_.z;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Pose pose : p_30563_.getDismountPoses()) {
            blockpos$mutableblockpos.set(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75D;

            while(true) {
                double d4 = this.level.getBlockFloorHeight(blockpos$mutableblockpos);
                if ((double)blockpos$mutableblockpos.getY() + d4 > d3) {
                    break;
                }

                if (DismountHelper.isBlockFloorValid(d4)) {
                    AABB aabb = p_30563_.getLocalBoundsForPose(pose);
                    Vec3 vec3 = new Vec3(d0, (double)blockpos$mutableblockpos.getY() + d4, d2);
                    if (DismountHelper.canDismountTo(this.level, p_30563_, aabb.move(vec3))) {
                        p_30563_.setPose(pose);
                        return vec3;
                    }
                }

                blockpos$mutableblockpos.move(Direction.UP);
                if (!((double)blockpos$mutableblockpos.getY() < d3)) {
                    break;
                }
            }
        }

        return null;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_30576_) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth(), p_30576_.getBbWidth(), this.getYRot() + (p_30576_.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F));
        Vec3 vec31 = this.getDismountLocationInDirection(vec3, p_30576_);
        if (vec31 != null) {
            return vec31;
        } else {
            Vec3 vec32 = getCollisionHorizontalEscapeVector(this.getBbWidth(), p_30576_.getBbWidth(), this.getYRot() + (p_30576_.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F));
            Vec3 vec33 = this.getDismountLocationInDirection(vec32, p_30576_);
            return vec33 != null ? vec33 : this.position();
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean canCollideWith(Entity p_38376_) {
        return canVehicleCollide(this, p_38376_);
    }

    public static boolean canVehicleCollide(Entity p_38324_, Entity p_38325_) {
        return (p_38325_.canBeCollidedWith() || p_38325_.isPushable()) && !p_38324_.isPassengerOfSameVehicle(p_38325_);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return true;
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        if (isAlive()) {
            WindDirectionSavedData data = BreezyNetworking.CLIENT_CACHE;

            if (data != null) {
                Direction direction = data.getWindDirection(blockPosition().getY(), level);

                if (getControllingPassenger() instanceof Player) {
                    if (!isOnGround() && getLitness() > 0) {
                        Vec3i normal = direction.getNormal();
                        setDeltaMovement(getDeltaMovement().add(normal.getX(), 0, normal.getZ()).scale(0.1F));
                    }

                    if (getLitness() > 0) {
                        setDeltaMovement(getDeltaMovement().add(0, (getLitness() + 1) * 0.02D, 0));

                        if (isOnGround()) {
                            setDeltaMovement(getDeltaMovement().add(0D, 1.0D, 0D));
                        }
                    }

                    if (!isOnGround() && getLitness() == 0) {
                        setDeltaMovement(0, -0.075D, 0);
                    }

                    if (getSandbags() > 0) {
                        setDeltaMovement(getDeltaMovement().subtract(0, (getSandbags() + 1) * 0.02D, 0));

                        /*if (isOnGround()) {
                            return;
                        }*/
                    }
                }
            }
            else {
                setDeltaMovement(0, -0.05, 0);
            }
            super.move(type, pos);
        }
    }

    public void setLitness(int litness) {
        this.entityData.set(LITNESS, litness);
    }

    public int getLitness() {
        return Math.min(this.entityData.get(LITNESS), 5);
    }

    public void setSandbags(int sandbags) {
        this.entityData.set(SANDBAGS, sandbags);
    }

    public int getSandbags() {
        return Math.min(this.entityData.get(SANDBAGS), 8);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.5D;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }
}
