package codyhuh.breezy.core.other.compat;

import codyhuh.breezy.core.other.tags.BreezyItemTags;
import codyhuh.breezy.core.other.util.CarpetBombUtil;
import galena.oreganized.index.OBlocks;
import galena.oreganized.index.OEntityTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class OreganizedCompat {

    public static void carpetBomb(PlayerInteractEvent.RightClickItem event) {
        ItemStack pen = ItemStack.EMPTY;
        ItemStack pineapple = ItemStack.EMPTY;

        for (ItemStack stack : event.getEntity().getHandSlots()) {
            if (stack.is(BreezyItemTags.IGNITION_SOURCES)) pen = stack;
            if (stack.is(OBlocks.SHRAPNEL_BOMB.get().asItem())) pineapple = stack;
        }

        InteractionResult result = CarpetBombUtil.carpetBomb(event, pen, pineapple, OEntityTypes.SHRAPNEL_BOMB.get());
        event.setCancellationResult(result);
    }
}