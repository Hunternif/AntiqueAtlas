package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;

import java.io.IOException;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PacketByteBuf;


/**
 * Sent from client to server to register a new
 * (unique tile name)-(pseudo-biome ID) pair.
 * @author Hunternif
 */
public class RegisterTileIdPacket extends AbstractServerMessage<RegisterTileIdPacket> {
	private String name;
	
	public RegisterTileIdPacket() {}
	
	public RegisterTileIdPacket(String uniqueTileName) {
		this.name = uniqueTileName;
	}
	
	@Override
	protected void read(PacketByteBuf buffer) throws IOException {
		name = buffer.readString(512);
	}

	@Override
	protected void write(PacketByteBuf buffer) throws IOException {
		buffer.writeString(name);
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		// Register the new tile id:
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(name);
		// Send it to all clients:
		TileNameIDPacket packet = new TileNameIDPacket();
		packet.put(name, biomeID);
		PacketDispatcher.sendToAll(((ServerWorld) player.getEntityWorld()).getServer(), packet);
	}
	
}
