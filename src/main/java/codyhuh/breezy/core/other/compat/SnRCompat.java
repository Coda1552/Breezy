package codyhuh.breezy.core.other.compat;

import codyhuh.breezy.core.other.tags.BreezyItemTags;
import codyhuh.breezy.core.other.util.CarpetBombUtil;
import com.teamabnormals.savage_and_ravage.core.registry.SRBlocks;
import com.teamabnormals.savage_and_ravage.core.registry.SREntityTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class SnRCompat {

    public static void carpetBomb(PlayerInteractEvent.RightClickItem event) {
        ItemStack pen = ItemStack.EMPTY;
        ItemStack pineapple = ItemStack.EMPTY;

        for (ItemStack stack : event.getEntity().getHandSlots()) {
            if (stack.is(BreezyItemTags.IGNITION_SOURCES)) pen = stack;
            if (stack.is(SRBlocks.SPORE_BOMB.get().asItem())) pineapple = stack;
        }

        InteractionResult result = CarpetBombUtil.carpetBomb(event, pen, pineapple, SREntityTypes.SPORE_BOMB.get());
        event.setCancellationResult(result);
    }
}
