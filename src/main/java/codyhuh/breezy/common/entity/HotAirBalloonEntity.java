package codyhuh.breezy.common.entity;

import codyhuh.breezy.BreezyConfig;
import codyhuh.breezy.common.network.BreezyNetworking;
import codyhuh.breezy.common.network.NewWindSavedData;
import codyhuh.breezy.core.other.tags.BreezyBiomeTags;
import codyhuh.breezy.core.other.tags.BreezyEntityTypeTags;
import codyhuh.breezy.core.other.tags.BreezyItemTags;
import codyhuh.breezy.core.other.util.WindMathUtil;
import codyhuh.breezy.core.registry.BreezyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
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
import java.util.Optional;

public class HotAirBalloonEntity extends LivingEntity implements GeoEntity {
    public static final AABB BASKET_AABB = new AABB(-0.7, 0, -0.7, 0.7, 1.0, 0.7);
    public static final AABB BALLOON_AABB = new AABB(-1.2, 2.4, -1.2, 1.2, 5, 1.2);

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
    public boolean hurt(@NotNull DamageSource source, float p_38320_) {
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
//        if (!this.level().isClientSide) {
//            peterPanParticles((ServerLevel) this.level(), BASKET_AABB);
//            peterPanParticles((ServerLevel) this.level(), BALLOON_AABB);
//        }
        if (tickCount % 10 == 0 && random.nextBoolean() && !this.getPassengers().isEmpty()) {
            Entity entity = this.getFirstPassenger();
            if (entity != null) {
                if (entity.getType().is(BreezyEntityTypeTags.HOT_ONES) && getLitness() != 3) {
                    setLitness(3);
                } else if (entity.isOnFire() && getLitness() < 2) {
                    setLitness(getLitness() + 1);
                }
            }
        }

        if (getLitness() > 0) {
            if (tickCount % (getLitness() * 80) == 0 && random.nextBoolean()) {
                setLitness(getLitness() - 1);
            }
            if (random.nextInt(8) == 0 && level() instanceof ServerLevel server) {
                Vec3 origin = boxInLevel(BALLOON_AABB).getCenter().subtract(0, 1.5, 0);
                server.sendParticles(ParticleTypes.SMOKE, origin.x, origin.y, origin.z, 1, 0, 0, 0, 0);
            }
        }

        if (isInWaterOrRain()) {
            Holder<Biome> holder = level().getBiome(blockPosition());
            if (holder.get().warmEnoughToRain(blockPosition()) && level().canSeeSky(blockPosition()) && !onGround()
            && level().isThundering() && random.nextInt(100) == 0 && level() instanceof ServerLevel server) {
                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level());
                if (lightningbolt != null) {
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(blockPosition().above()));
                    server.addFreshEntity(lightningbolt);
                }
            }
            if (random.nextInt(20) == 0) {
                setLitness(getLitness() - 1);
            }
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate((double) 0.2F, (double) -0.01F, (double) 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    AABB validBasket = BASKET_AABB.inflate(-0.2, 0.72, -0.2);
                    if (flag && this.getPassengers().isEmpty() && !entity.isPassenger() &&
                            entity.getBbWidth() < validBasket.getXsize() && entity instanceof LivingEntity &&
                            !(entity instanceof WaterAnimal) && !(entity instanceof Player)
                            && entity.getBbHeight() < validBasket.getYsize()) {
                        entity.startRiding(this);
                    } else if (boxInLevel(BALLOON_AABB).intersects(entity.getBoundingBox()) ||
                            boxInLevel(BASKET_AABB).intersects(entity.getBoundingBox())) {
                        this.push(entity);
                    }
                }
            }
        }
    }

    private void peterPanParticles(ServerLevel level, AABB box) {
        if (level.random.nextBoolean()) return;
        for (int i = 0; i < 1; i++) {
            double x = box.minX + (box.maxX - box.minX) * random.nextDouble();
            double y = box.minY + (box.maxY - box.minY) * random.nextDouble();
            double z = box.minZ + (box.maxZ - box.minZ) * random.nextDouble();
            level.sendParticles(ParticleTypes.END_ROD, this.getX() + x, this.getY() + y, this.getZ() + z, 1, 0, 0, 0, 0);
        }
    }

    protected void pushEntities() {
        theRealPush(boxInLevel(BALLOON_AABB));
        theRealPush(boxInLevel(BASKET_AABB));
    }

    private void theRealPush(AABB box) {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), box, EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> list = this.level().getEntities(this, box, EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
                if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for (Entity entity : list) {
                        if (!entity.isPassenger()) {
                            ++j;
                        }
                    }

                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }
                for (Entity entity : list) {
                    this.doPush(entity);
                }
            }
        }
    }

    public void push(double x, double y, double z) {
        this.setDeltaMovement(this.getDeltaMovement().add(x * 0.8, y * 0.8, z * 0.8));
        this.hasImpulse = true;
    }

    protected float tickHeadTurn(float p_21260_, float p_21261_) {
        return p_21261_;
    }

    @Override
    public void travel(@NotNull Vec3 lookVectorMaybe) {
        if (isAlive()) {
            NewWindSavedData data = BreezyNetworking.CLIENT_CACHE;
            if (data != null) {
                Vec3 targetVel = getTargetDirection(data);
                Vec3 lerpedVel = WindMathUtil.vec3Lerp(getDeltaMovement(), targetVel, 0.06F);
                setDeltaMovement(lerpedVel);
            }
            super.travel(lookVectorMaybe);
        }
    }

    public boolean canStandOnFluid(FluidState fluidState) {
        return fluidState.is(FluidTags.WATER);
    }

    public Vec3 getTargetDirection(NewWindSavedData data) {
        double direction = data.getWindAtHeight(blockPosition().getY(), level());
        Holder<Biome> holder = level().getBiome(blockPosition());
        int litness = getLitness();
        Vec3 wind = new Vec3(WindMathUtil.stepX(direction), 0.0, WindMathUtil.stepZ(direction)).scale(0.4F);
        double uplift = 0;

        if (litness > 0) {
            uplift += (litness + 1) * 0.03D;
            if (onGround()) {
                setOnGround(false);
            }
        }
        if (isInPowderSnow || isInWaterOrBubble()) {
            return new Vec3(0, -0.075, 0);
        }
        if (litness == 0) {
            wind.scale(0.5);
            if (onGround()) {
                return new Vec3(0, -0.075, 0);
            } else {
                uplift -= 0.075D;
            }
        }
        if (!level().canSeeSky(blockPosition())) wind.scale(0.4);
        if (getSandbags() > 0) {
            uplift -= (getSandbags() + 1) * 0.02D;
        }
        if (getY() >= level().getMaxBuildHeight() - 12) {
            uplift = 0;
        }

        double biomePenalty = 0.7;
        double biomeBonus = 1.6;
        wind.add(0, uplift, 0);
        if (holder.is(BreezyBiomeTags.NO_WIND)) {
            return new Vec3(0, uplift, 0);
        } else {
            if (holder.is(BreezyBiomeTags.LESS_WIND)) {
                wind.scale(biomePenalty);
            } else if (holder.is(BreezyBiomeTags.MORE_WIND)) {
                wind.scale(biomeBonus);
            }
        }
        double altitudeBonus = (data.getLayer(blockPosition().getY(), level()) * .1);
        wind.add(altitudeBonus, 0, altitudeBonus);
        return new Vec3(wind.x, uplift, wind.z);
    }

    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    public boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return false;
    }

    @Override
    @NotNull
    public InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        return this.interactionBusiness(player, hand,
                isLookingAtHitbox(player, boxInLevel(BASKET_AABB)), isLookingAtHitbox(player, boxInLevel(BALLOON_AABB)));
    }

    boolean isLookingAtHitbox(Player player, AABB box) {
        Vec3 playerEyePosition = player.getEyePosition(1.0F);
        Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
        Vec3 lookTarget = playerEyePosition.add(playerViewVector.scale(player.getEntityReach()));

        Optional<Vec3> intersection = box.clip(playerEyePosition, lookTarget);
        return intersection.isPresent();
    }


    public AABB boxInLevel(AABB box) {
        return new AABB(box.minX + this.getX(), box.minY + getY(), box.minZ + getZ(),
                box.maxX + getX(), box.maxY + getY(), box.maxZ + getZ());
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        return this.interactAt(player, Vec3.ZERO, hand);
    }

    public InteractionResult interactionBusiness(Player player, InteractionHand hand, boolean basket, boolean balloon) {
        if (!basket && !balloon && !this.getPassengers().isEmpty() && !this.hasPassenger(player)) {
            Entity rider = getPassengers().get(0);
            return player.interactOn(rider, hand);
        }
        ItemStack itemstack = player.getItemInHand(hand);
        if (balloon) {
            if (getLitness() < 5) {
                if (itemstack.is(BreezyItemTags.IGNITION_SOURCES)) {
                    setLitness(getLitness() + 2);
                    playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0F, 1.0F);
                    Vec3 origin = boxInLevel(BALLOON_AABB).getCenter().subtract(0, 1.85, 0);
                    for (int i = 0; i < 5; i++) {
                        level().addParticle(ParticleTypes.LAVA, origin.x, origin.y, origin.z, 0, 0, 0);
                    }
                    itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    return InteractionResult.SUCCESS;
                }
            }
            Item item = itemstack.getItem();
            if (item instanceof DyeItem dye) {
                int dyecolor = dye.getDyeColor().getFireworkColor();
                if (dyecolor != this.getColor()) {
                    this.dyeBalloon(dye);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (basket) {
            if (player.getItemInHand(hand).is(ItemTags.SAND)) {
                if (getSandbags() < 8) {
                    setSandbags(getSandbags() + 1);
                    playSound(SoundEvents.SAND_PLACE, 1.0F, 1.5F);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.CONSUME;
                }
            }
            if (getSandbags() > 0 && player.getItemInHand(hand).is(Tags.Items.SHEARS)) {
                setSandbags(getSandbags() - 1);
                playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

                return InteractionResult.SUCCESS;
            }
            if (getPassengers().isEmpty()) {
                if (!this.level().isClientSide) {
                    return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void dyeBalloon(DyeItem dye) {
        int[] aint = new int[3];
        int i = 0;
        int j = 2;
        int k = this.getColor();
        float f = (float) (k >> 16 & 255) / 255.0F;
        float f1 = (float) (k >> 8 & 255) / 255.0F;
        float f2 = (float) (k & 255) / 255.0F;
        i += (int) (Math.max(f, Math.max(f1, f2)) * 255.0F);
        aint[0] += (int) (f * 255.0F);
        aint[1] += (int) (f1 * 255.0F);
        aint[2] += (int) (f2 * 255.0F);

        float[] afloat = dye.getDyeColor().getTextureDiffuseColors();
        int i2 = (int) (afloat[0] * 255.0F);
        int l = (int) (afloat[1] * 255.0F);
        int i1 = (int) (afloat[2] * 255.0F);
        i += Math.max(i2, Math.max(l, i1));
        aint[0] += i2;
        aint[1] += l;
        aint[2] += i1;

        int j1 = aint[0] / j;
        int k1 = aint[1] / j;
        int l1 = aint[2] / j;
        float f3 = (float) i / (float) j;
        float f4 = (float) Math.max(j1, Math.max(k1, l1));
        j1 = (int) ((float) j1 * f3 / f4);
        k1 = (int) ((float) k1 * f3 / f4);
        l1 = (int) ((float) l1 * f3 / f4);
        int j2 = (j1 << 8) + k1;
        j2 = (j2 << 8) + l1;
        this.setColor(j2);
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
            double d3 = this.getBoundingBox().maxY - 3.5;

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
    public @NotNull Vec3 getDismountLocationForPassenger(LivingEntity living) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth(), living.getBbWidth(), this.getYRot() + (living.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F));
        Vec3 vec31 = this.getDismountLocationInDirection(vec3, living);
        if (vec31 != null) {
            return vec31;
        } else {
            Vec3 vec32 = getCollisionHorizontalEscapeVector(this.getBbWidth(), living.getBbWidth(), this.getYRot() + (living.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F));
            Vec3 vec33 = this.getDismountLocationInDirection(vec32, living);
            return vec33 != null ? vec33 : this.position();
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean canCollideWith(@NotNull Entity p_38376_) {
        return canVehicleCollide(this, p_38376_);
    }

    public static boolean canVehicleCollide(Entity p_38324_, Entity p_38325_) {
        return (p_38325_.canBeCollidedWith() || p_38325_.isPushable()) && !p_38324_.isPassengerOfSameVehicle(p_38325_);
    }

    public boolean isPushable() {
        return getPassengers().isEmpty();
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return true;
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CustomColor", this.getColor());
    }

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("CustomColor", 99)) {
            this.setColor((tag.getInt("CustomColor")));
        }
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setColor(int p_30398_) {
        this.entityData.set(DATA_COLOR, p_30398_);
    }

    public void setLitness(int litness) {
        this.entityData.set(LITNESS, Math.max(0, litness));
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot p_21036_, @NotNull ItemStack p_21037_) {
    }

    @Override
    public double getPassengersRidingOffset() {
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Skeleton) {
            return 0.65;
        }
        return 0.5D;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return BreezyConfig.CLIENT.balloonsAlwaysRender.get() || super.shouldRenderAtSqrDistance(p_33107_);
    }

    public boolean addEffect(@NotNull MobEffectInstance p_182397_, @javax.annotation.Nullable Entity p_182398_) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.2, 0.2, 0.2);
    }

    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(BreezyItems.HOT_AIR_BALLOON.get());
    }

    public boolean startRiding(Entity p_19966_, boolean p_19967_) {
        return false;
    }

    public boolean shouldShowName() {
        return false;
    }

    public boolean showVehicleHealth() {
        return false;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}