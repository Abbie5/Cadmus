package earth.terrarium.cadmus.common.teams;

import com.mojang.authlib.GameProfile;
import earth.terrarium.cadmus.api.claims.InteractionType;
import earth.terrarium.cadmus.api.teams.TeamProvider;
import earth.terrarium.cadmus.common.claims.ClaimInfo;
import earth.terrarium.cadmus.common.claims.ClaimSaveData;
import earth.terrarium.cadmus.common.claims.ClaimType;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanillaTeamProvider implements TeamProvider {
    @Override
    public Set<GameProfile> getTeamMembers(String id, MinecraftServer server, GameProfile creator) {
        PlayerTeam team = server.getScoreboard().getPlayersTeam(id);
        Set<GameProfile> profiles = new HashSet<>();
        if (team == null) {
            profiles.add(creator);
            return profiles;
        }
        for (String player : team.getPlayers()) {
            server.getProfileCache().get(player).ifPresent(profiles::add);
        }
        return profiles;
    }

    @Override
    @Nullable
    public String getTeamName(String id, MinecraftServer server) {
        PlayerTeam team = server.getScoreboard().getPlayersTeam(id);
        return Optionull.map(team, PlayerTeam::getName);
    }

    @Override
    public boolean isMember(String id, MinecraftServer server, UUID player) {
        PlayerTeam team = server.getScoreboard().getPlayerTeam(id);
        GameProfile profile = server.getProfileCache().get(player).orElse(null);
        if (profile == null || team == null) return false;
        return team.getPlayers().contains(profile.getName());
    }

    @Override
    public boolean canBreakBlock(String id, MinecraftServer server, BlockPos pos, UUID player) {
        return canAccess(id, server, player);
    }

    @Override
    public boolean canPlaceBlock(String id, MinecraftServer server, BlockPos pos, UUID player) {
        return canAccess(id, server, player);
    }

    @Override
    public boolean canExplodeBlock(String id, MinecraftServer server, BlockPos pos, Explosion explosion, UUID player) {
        return canAccess(id, server, player);
    }

    @Override
    public boolean canInteractWithBlock(String id, MinecraftServer server, BlockPos pos, InteractionType type, UUID player) {
        return canAccess(id, server, player);
    }

    @Override
    public boolean canInteractWithEntity(String id, MinecraftServer server, Entity entity, UUID player) {
        return canAccess(id, server, player);
    }

    @Override
    public boolean canDamageEntity(String id, MinecraftServer server, Entity entity, UUID player) {
        return canAccess(id, server, player);
    }

    private boolean canAccess(String id, MinecraftServer server, UUID playerId) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player == null || player.getTeam() == null) {
            return true;
        }
        return player.getTeam().getName().equals(id);
    }

    public void addPlayerToTeam(MinecraftServer server, String playerName, PlayerTeam scoreboardTeam) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player == null) return;

        Set<ChunkPos> removed = new HashSet<>();
        for (Team team : TeamSaveData.getTeams(server)) {
            if (team.name().equals(scoreboardTeam.getName())) {
                TeamSaveData.addTeamMember(player, team);
                return;
            }
            removed.addAll(TeamSaveData.removeTeamMember(player, team));
        }
        Team team = TeamSaveData.getOrCreateTeam(player, scoreboardTeam.getName(), Component.literal(scoreboardTeam.getName()));
        // Transfer chunks to new team if the old team was removed
        removed.forEach(chunkPos -> ClaimSaveData.set(player.getLevel(), chunkPos, new ClaimInfo(team.teamId(), ClaimType.CLAIMED)));
    }

    public void removePlayerFromTeam(MinecraftServer server, String playerName, PlayerTeam scoreboardTeam) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player == null) return;

        for (Team team : TeamSaveData.getTeams(server)) {
            if (team.name().equals(scoreboardTeam.getName())) {
                TeamSaveData.removeTeamMember(player, team);
                return;
            }
        }
    }
}