package coda.breezy.registry;

import coda.breezy.Breezy;
import coda.breezy.common.items.GustGaugeItem;
import coda.breezy.common.items.HotAirBalloonItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BreezyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Breezy.MOD_ID);

    public static final RegistryObject<Item> HOT_AIR_BALLOON = ITEMS.register("hot_air_balloon", () -> new HotAirBalloonItem(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1)));
    public static final RegistryObject<Item> GUST_GAUGE = ITEMS.register("gust_gauge", () -> new GustGaugeItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)));
}
