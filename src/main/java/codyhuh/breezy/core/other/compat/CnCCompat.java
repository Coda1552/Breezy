package codyhuh.breezy.core.other.compat;

import codyhuh.breezy.core.other.tags.BreezyItemTags;
import codyhuh.breezy.core.other.util.CarpetBombUtil;
import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import com.teamabnormals.caverns_and_chasms.core.registry.CCEntityTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CnCCompat {

    public static void carpetBomb(PlayerInteractEvent.RightClickItem event) {
        ItemStack pen = ItemStack.EMPTY;
        ItemStack pineapple = ItemStack.EMPTY;

        for (ItemStack stack : event.getEntity().getHandSlots()) {
            if (stack.is(BreezyItemTags.IGNITION_SOURCES)) pen = stack;
            if (stack.is(CCBlocks.TMT.get().asItem())) pineapple = stack;
        }

        InteractionResult result = CarpetBombUtil.carpetBomb(event, pen, pineapple, CCEntityTypes.TMT.get());
        event.setCancellationResult(result);
    }
}