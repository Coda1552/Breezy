package codyhuh.breezy.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindDirectionPacket {

	private final NewWindSavedData data;

	public WindDirectionPacket(NewWindSavedData data) {
		this.data = data;
	}

	public WindDirectionPacket(FriendlyByteBuf buf) {
		data = new NewWindSavedData(buf.readAnySizeNbt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(data.save(new CompoundTag()));
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		// access to ClientLevel is directly exposed *here*
		BreezyNetworking.CLIENT_CACHE = data;
		return true;
	}
}