package codyhuh.breezy.common.item;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import codyhuh.breezy.core.registry.BreezyEntities;
import codyhuh.breezy.core.registry.BreezyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.DEFAULT_COLOR;

public class HotAirBalloonItem extends Item implements DyeableLeatherItem {

    public HotAirBalloonItem(Properties p_41383_) {
        super(p_41383_);

    }

    public int getColor(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99)
                ? compoundtag.getInt("color") : DEFAULT_COLOR;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else if (context.getItemInHand().getItem().equals(BreezyItems.HOT_AIR_BALLOON.get())) {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction, 1);
            }
            HotAirBalloonEntity entity = BreezyEntities.HOT_AIR_BALLOON.get().create(context.getLevel());
            if (entity == null) return InteractionResult.FAIL;

            entity.moveTo(blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5, Objects.requireNonNull(context.getPlayer()).yRotO, 0f);
            int color = getColor(itemstack);
            entity.setColor(color);
            entity.setDeltaMovement(Vec3.ZERO);
            if (context.getClickedFace() == Direction.UP) {
                entity.setOnGround(true);
            }
            if (context.getLevel().addFreshEntity(entity) && !context.getPlayer().isCreative()) {
                itemstack.shrink(1);
            }
            context.getLevel().playSound(null, entity, SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 1, 1);

            return InteractionResult.CONSUME;
        }
        else {
            return super.useOn(context);
        }
    }
}
