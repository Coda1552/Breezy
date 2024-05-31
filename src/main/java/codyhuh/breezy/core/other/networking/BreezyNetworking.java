package codyhuh.breezy.core.other.networking;

import codyhuh.breezy.Breezy;
import codyhuh.breezy.common.WindDirectionSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class BreezyNetworking {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    
    public static WindDirectionSavedData CLIENT_CACHE = null;
    
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Breezy.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(WindDirectionPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(WindDirectionPacket::new)
                .encoder(WindDirectionPacket::write)
                .consumerMainThread(WindDirectionPacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message) {
        if (Minecraft.getInstance().getConnection() != null) {
            INSTANCE.sendTo(message, Minecraft.getInstance().getConnection().getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
