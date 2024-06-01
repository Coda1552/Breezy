package codyhuh.breezy.common.entities;

import codyhuh.breezy.BreezyConfig;
import codyhuh.breezy.common.WindDirectionSavedData;
import codyhuh.breezy.core.other.networking.BreezyNetworking;
import codyhuh.breezy.core.other.tags.BreezyBiomeTags;
import codyhuh.breezy.core.other.tags.BreezyEntityTypeTags;
import codyhuh.breezy.core.registry.BreezyItems;
import codyhuh.breezy.core.other.tags.BreezyItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collections;
import java.util.List;

public class HotAirBalloonEntity extends LivingEntity implements GeoEntity {
    public static final int DEFAULT_COLOR = 16351261;

    private static final EntityDataAccessor<Integer> LITNESS = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SANDBAGS = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(HotAirBalloonEntity.class, EntityDataSerializers.FLOAT);

    public HotAirBalloonEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5F).add(Attributes.ATTACK_DAMAGE, 0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LITNESS, 0);
        this.entityData.define(SANDBAGS, 0);
        this.entityData.define(DATA_COLOR, DEFAULT_COLOR);
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    public void setDamage(float p_38110_) {
        this.entityData.set(DATA_ID_DAMAGE, p_38110_);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setHurtTime(int p_38155_) {
        this.entityData.set(DATA_ID_HURT, p_38155_);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtDir(int p_38161_) {
        this.entityData.set(DATA_ID_HURTDIR, p_38161_);
    }

    public int getHurtDirection() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    @Override
    public void animateHurt(float p_265265_) {
        this.setHurtDir(-this.getHurtDirection());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float p_38320_) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDirection());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + p_38320_ * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamage() > 40.0F) {
                if (!flag && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.destroy();
                }

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    public void destroy() {
        this.discard();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(BreezyItems.HOT_AIR_BALLOON.get());
            if (this.hasCustomName()) {
                itemstack.setHoverName(this.getCustomName());
            }
            if (this.getColor() != DEFAULT_COLOR) {
                CompoundTag compoundtag = itemstack.getOrCreateTagElement("display");
                compoundtag.putInt("color", this.getColor());
            }

            this.spawnAtLocation(itemstack);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 10 == 0 && random.nextBoolean() && !this.getPassengers().isEmpty())  {
            Entity entity = this.getPassengers().get(0);
            if (entity.getType().is(BreezyEntityTypeTags.HOT_ONES) && getLitness() != 3) {
                setLitness(3);
            } else if (entity.isOnFire() && getLitness() < 2){
                setLitness(getLitness() + 1);
            }
        }

        if (getLitness() > 0 && tickCount % (getLitness() * 40) == 0 && random.nextBoolean()) {
            setLitness(getLitness() - 1);
        }

        if (isInWaterOrRain()) {
            setLitness(0);
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate((double)0.2F, (double)-0.01F, (double)0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (flag && this.getPassengers().isEmpty() && !entity.isPassenger() &&
                            entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity &&
                            !(entity instanceof WaterAnimal) && !(entity instanceof Player)
                    && entity.getBbHeight() < this.getBbHeight() * 2.0) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }
    }

    @Override
    public void travel(Vec3 p_21280_) {
        if (isAlive()) {
            WindDirectionSavedData data = BreezyNetworking.CLIENT_CACHE;
            Holder<Biome> holder = level().getBiome(blockPosition());
            if (data != null) {
                Direction direction = data.getWindDirection(blockPosition().getY(), level());
                if (!onGround() && getLitness() > 0) {
                    Vec3i normal = direction.getNormal();
                    setDeltaMovement(getDeltaMovement().add(normal.getX(), 0, normal.getZ()).scale(0.1F));
                }
                if (getLitness() > 0) {
                    setDeltaMovement(getDeltaMovement().add(0, (getLitness() + 1) * 0.02D, 0));
                    if (onGround()) {
                        setOnGround(false);
                        setDeltaMovement(getDeltaMovement().add(0D, 0.02D, 0D));
                    }
                }
                if (!onGround() && getLitness() == 0) {
                    setDeltaMovement(0, -0.075D, 0);
                }
                if (getSandbags() > 0) {
                    setDeltaMovement(getDeltaMovement().subtract(0, (getSandbags() + 1) * 0.02D, 0));
                }
            } else {
                if (holder.is(BreezyBiomeTags.NO_WIND)) {
                    setDeltaMovement(0, getDeltaMovement().y, 0);
                } else {
                    setDeltaMovement(0, -0.075D, 0);
                }
            }
            super.travel(p_21280_);
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
//        if (item instanceof DyeItem dye) {
//            DyeColor dyecolor = dye.getDyeColor();
//            if (dyecolor != this.getColor()) {
//                this.setColor(dyecolor);
//                if (!player.getAbilities().instabuild) {
//                    itemstack.shrink(1);
//                }
//                return InteractionResult.SUCCESS;
//            }
//        }
        if (!hasPassenger(player) && player.getItemInHand(hand).isEmpty()) {
            if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
//        if (getLitness() < 5 && isVehicle() && getControllingPassenger().is(player)) {
        if (getLitness() < 5) {
            if (itemstack.is(BreezyItemTags.IGNITION_SOURCES)) {
                setLitness(getLitness() + 1);
                playSound(SoundEvents.CAMPFIRE_CRACKLE, 1.0F, 1.0F);
                itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            }
        }

        if (getSandbags() < 8 && player.getItemInHand(hand).is(ItemTags.SAND)) {
            setSandbags(getSandbags() + 1);
            playSound(SoundEvents.SAND_PLACE, 1.0F, 1.0F);

            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        if (getSandbags() > 0 && player.getItemInHand(hand).is(Tags.Items.SHEARS)) {
            setSandbags(getSandbags() - 1);
            playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().get(0);
    }

    @Nullable
    private Vec3 getDismountLocationInDirection(Vec3 p_30562_, LivingEntity p_30563_) {
        double d0 = this.getX() + p_30562_.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.getZ() + p_30562_.z;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Pose pose : p_30563_.getDismountPoses()) {
            blockpos$mutableblockpos.set(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75D;

            while (true) {
                double d4 = this.level().getBlockFloorHeight(blockpos$mutableblockpos);
                if ((double) blockpos$mutableblockpos.getY() + d4 > d3) {
                    break;
                }

                if (DismountHelper.isBlockFloorValid(d4)) {
                    AABB aabb = p_30563_.getLocalBoundsForPose(pose);
                    Vec3 vec3 = new Vec3(d0, (double) blockpos$mutableblockpos.getY() + d4, d2);
                    if (DismountHelper.canDismountTo(this.level(), p_30563_, aabb.move(vec3))) {
                        p_30563_.setPose(pose);
                        return vec3;
                    }
                }

                blockpos$mutableblockpos.move(Direction.UP);
                if (!((double) blockpos$mutableblockpos.getY() < d3)) {
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
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

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return true;
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("customColor", this.getColor());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("customColor", 99)) {
            this.setColor((tag.getInt("customColor")));
        }
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setColor(int p_30398_) {
        this.entityData.set(DATA_COLOR, p_30398_);
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
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.35D;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return BreezyConfig.CLIENT.balloonsAlwaysRender.get() || super.shouldRenderAtSqrDistance(p_33107_);
    }

    public boolean addEffect(MobEffectInstance p_182397_, @javax.annotation.Nullable Entity p_182398_) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.2, 3.0, 0.2);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
