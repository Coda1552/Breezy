package codyhuh.breezy;

import codyhuh.breezy.common.entity.HotAirBalloonEntity;
import codyhuh.breezy.core.data.server.BreezyBiomeTagsProvider;
import codyhuh.breezy.core.data.server.BreezyEntityTypeTagsProvider;
import codyhuh.breezy.common.network.BreezyNetworking;
import codyhuh.breezy.common.network.WindDirectionPacket;
import codyhuh.breezy.common.network.NewWindSavedData;
import codyhuh.breezy.core.other.tags.BreezyItemTags;
import codyhuh.breezy.core.other.util.HitBoxUtil;
import codyhuh.breezy.core.registry.BreezyEntities;
import codyhuh.breezy.core.registry.BreezyItems;
import codyhuh.breezy.core.registry.BreezyParticles;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static codyhuh.breezy.common.entity.HotAirBalloonEntity.BALLOON_AABB;

@Mod(Breezy.MOD_ID)
public class Breezy {
    public static final String MOD_ID = "breezy";
    public static final Logger LOGGER = LogManager.getLogger();

    public Breezy() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext context = ModLoadingContext.get();

        bus.addListener(this::commonSetup);
        bus.addListener(this::dataSetup);
        bus.addListener(this::registerEntityAttributes);
        bus.addListener(this::populateTabs);

        forgeBus.addListener(this::resetWindDirection);
        forgeBus.addListener(this::syncWindDataOnJoinWorld);
        forgeBus.addListener(this::tntDrop);

        BreezyParticles.PARTICLES.register(bus);
        BreezyEntities.ENTITIES.register(bus);
        BreezyItems.ITEMS.register(bus);

        context.registerConfig(ModConfig.Type.COMMON, BreezyConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, BreezyConfig.CLIENT_SPEC);
    }

    private void populateTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (var item : BreezyItems.ITEMS.getEntries()) {
                e.accept(item.get());
            }
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent e) {
        e.put(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonEntity.createAttributes().build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BreezyNetworking.register();
    }

    private void dataSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        boolean server = event.includeServer();

        generator.addProvider(server, new BreezyBiomeTagsProvider(output, provider, helper));
        generator.addProvider(server, new BreezyEntityTypeTagsProvider(output, provider, helper));
    }

    private void resetWindDirection(TickEvent.LevelTickEvent e) {
        Level world = e.level;

        if (!world.isClientSide && world.getGameTime() % BreezyConfig.COMMON.windPeriodLength.get() == 0) {
            NewWindSavedData.resetWindDirections(BreezyConfig.COMMON.windPeriodLength.get(),
                    BreezyConfig.COMMON.changePercentage.get());
            world.players().forEach(player -> {
                Level level = player.level();
                if (level.isClientSide) return;
                NewWindSavedData data = ((ServerLevel) level).getDataStorage()
                        .computeIfAbsent(NewWindSavedData::new,
                                () -> new NewWindSavedData(((ServerLevel) level).getSeed()), Breezy.MOD_ID + ".savedata");
                BreezyNetworking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
            });
        }
    }

    public void syncWindDataOnJoinWorld(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof Player player && !e.getLevel().isClientSide) {
            NewWindSavedData data = ((ServerLevel) player.level()).getDataStorage().computeIfAbsent(NewWindSavedData::new,
                    () -> new NewWindSavedData(((ServerLevel) player.level()).getSeed()), Breezy.MOD_ID + ".savedata");
            BreezyNetworking.sendToPlayer(new WindDirectionPacket(data), (ServerPlayer) player);
        }
    }

    public void tntDrop(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack pen = ItemStack.EMPTY;
        ItemStack pineapple = ItemStack.EMPTY;
        for (ItemStack stack : player.getHandSlots()) {
            if (stack.is(BreezyItemTags.IGNITION_SOURCES)) pen = stack;
            if (stack.is(Items.TNT)) pineapple = stack;
        }
        if (player.getVehicle() instanceof HotAirBalloonEntity balloon && !balloon.onGround() &&
                HitBoxUtil.isNotAimingAtHitbox(player, HitBoxUtil.boxInLevel(BALLOON_AABB, balloon),
                        player.getViewVector((float) player.getEntityReach())) && !pen.isEmpty() && !pineapple.isEmpty()) {
            if (event.getLevel() instanceof ServerLevel server) {
                PrimedTnt tnt = EntityType.TNT.create(event.getLevel());
                if (tnt != null) {
                    tnt.moveTo(Vec3.atBottomCenterOf(player.blockPosition().above()));
                    server.addFreshEntity(tnt);
                    balloon.playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        pineapple.shrink(1);
                        pen.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    }
                }
            } else {
                player.swing(event.getHand());
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}