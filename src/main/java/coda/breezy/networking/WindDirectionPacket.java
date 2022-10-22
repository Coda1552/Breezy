package coda.breezy.networking;

import coda.breezy.common.WindDirectionSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindDirectionPacket {
	
	private final WindDirectionSavedData data;
	
	public WindDirectionPacket(WindDirectionSavedData data) {
		this.data = data;
	}
	
	public WindDirectionPacket(FriendlyByteBuf buf) {
		data = new WindDirectionSavedData(buf.readAnySizeNbt());
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(data.save(new CompoundTag()));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		BreezyNetowrking.CLIENT_CACHE = data;
		return true;
	}
}