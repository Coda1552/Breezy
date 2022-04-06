package coda.whooosh.registry;

import coda.whooosh.Whooosh;
import coda.whooosh.common.items.HotAirBalloonItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WhoooshItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Whooosh.MOD_ID);

    public static final RegistryObject<Item> HOT_AIR_BALLOON = ITEMS.register("hot_air_balloon", () -> new HotAirBalloonItem(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1)));
}
