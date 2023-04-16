package earth.terrarium.cadmus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.common.claims.ClaimChunkSaveData;
import earth.terrarium.cadmus.common.claims.ClaimInfo;
import earth.terrarium.cadmus.common.claims.ClaimType;
import earth.terrarium.cadmus.common.team.Team;
import earth.terrarium.cadmus.common.team.TeamSaveData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record UpdateClaimedChunksPacket(Map<ChunkPos, ClaimType> addedChunks,
                                        Set<ChunkPos> removedChunks) implements Packet<UpdateClaimedChunksPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Cadmus.MOD_ID, "update_claimed_chunks");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<UpdateClaimedChunksPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<UpdateClaimedChunksPacket> {
        @Override
        public void encode(UpdateClaimedChunksPacket packet, FriendlyByteBuf buf) {
            buf.writeMap(packet.addedChunks, FriendlyByteBuf::writeChunkPos, FriendlyByteBuf::writeEnum);
            buf.writeCollection(packet.removedChunks, FriendlyByteBuf::writeChunkPos);
        }

        @Override
        public UpdateClaimedChunksPacket decode(FriendlyByteBuf buf) {
            Map<ChunkPos, ClaimType> addedChunks = buf.readMap(HashMap::new, FriendlyByteBuf::readChunkPos, buf1 -> buf1.readEnum(ClaimType.class));
            Set<ChunkPos> removedChunks = buf.readCollection(HashSet::new, FriendlyByteBuf::readChunkPos);
            return new UpdateClaimedChunksPacket(addedChunks, removedChunks);
        }

        @Override
        public PacketContext handle(UpdateClaimedChunksPacket message) {
            return (player, level) -> {
                Team team = TeamSaveData.getOrCreate((ServerPlayer) player);
                message.addedChunks.forEach((pos, type) -> {
                    ClaimChunkSaveData.set(player.level, pos, new ClaimInfo(team, type));
                });
                message.removedChunks.forEach(chunkPos -> ClaimChunkSaveData.remove(player.level, chunkPos));
            };
        }
    }
}