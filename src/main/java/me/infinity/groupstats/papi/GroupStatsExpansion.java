package me.infinity.groupstats.papi;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.GroupStatsPlugin;
import me.infinity.groupstats.database.profile.GroupProfile;
import me.infinity.groupstats.util.GroupStatsTable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GroupStatsExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "bedwars1058_group_stats";
    }

    @Override
    public @NotNull String getAuthor() {
        return "I6y";
    }

    @Override
    public @NotNull String getVersion() {
        return GroupStatsPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        return super.onRequest(player, params);
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "INVALID_PLAYER";

        String[] args = params.split("_");
        String group = args[0];
        Dao<GroupProfile, UUID> dao = DaoManager.lookupDao(GroupStatsPlugin.getInstance().getDatabaseFactory().getConnectionSource(), GroupStatsTable.getDatabaseTableConfig(group));
        GroupProfile groupProfile = GroupStatsPlugin.getInstance().getDatabaseFactory().getGroupProfileFactory().getCache().get(group).get(player.getUniqueId());

        switch (args[1]) {
            case "gamesplayed":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getGamesPlayed());
            case "bedsbroken":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getBedsBroken());
            case "bedslost":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getBedsLost());
            case "kills":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getKills());
            case "deaths":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getDeaths());
            case "finalkills":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getFinalKills());
            case "finalDeaths":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getFinalDeaths());
            case "wins":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getWins());
            case "losses":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getLosses());
            case "winstreak":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getWinstreak());
            case "highestwinstreak":
                return String.valueOf(groupProfile == null ? 0 : groupProfile.getHighestWinstreak());
            case "kdr":
                return String.valueOf(groupProfile == null ? 0.0 : (double) groupProfile.getKills()/groupProfile.getDeaths());
            case "finalkdr":
                return String.valueOf(groupProfile == null ? 0.0 : (double) groupProfile.getFinalKills()/groupProfile.getFinalDeaths());
        }
        return "INVALID_PLACEHOLDER";

    }
}
