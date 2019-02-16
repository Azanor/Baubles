package baubles.common.network;

import baubles.api.cap.BaublesCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSync {

	private final int playerId;
	private final byte slot;
	private final ItemStack bauble;

	public PacketSync(EntityPlayer p, int slot, ItemStack bauble) {
		this(p.getEntityId(), slot, bauble);
	}

	private PacketSync(int entity, int slot, ItemStack bauble) {
		this.slot = (byte) slot;
		this.bauble = bauble;
		this.playerId = entity;
	}

	public static void encode(PacketSync msg, PacketBuffer buffer) {
		buffer.writeInt(msg.playerId);
		buffer.writeByte(msg.slot);
		buffer.writeItemStack(msg.bauble);
	}

	public static PacketSync decode(PacketBuffer buffer) {
		return new PacketSync(buffer.readInt(), buffer.readByte(), buffer.readItemStack());
	}

	public static void handle(PacketSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Entity p = Minecraft.getInstance().world.getEntityByID(message.playerId);
			if (p instanceof EntityPlayer) {
				p.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES).ifPresent(b -> {
					b.setStackInSlot(message.slot, message.bauble);
				});
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
