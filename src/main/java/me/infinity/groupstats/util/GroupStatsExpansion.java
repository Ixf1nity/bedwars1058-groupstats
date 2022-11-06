package me.infinity.groupstats.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.GroupStatsPlugin;
import me.infinity.groupstats.profile.GroupProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class GroupStatsExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "groupstats";
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        String result;
        String[] args = params.split("_");
        if (args[0] == null || args[1] == null) {
            return "INVALID_PLACEHOLDER";
        }
        String groupName = args[0]; // %groupstats_Solo_kills% --- 'Solo'
        String statisticType = args[1]; // %groupstats_Solo_kills% --- 'kills'

        GroupProfile profile = null;
        try {
            profile = GroupStatsPlugin
                    .getInstance()
                    .getDatabaseFactory()
                    .getGroupProfileFactory()
                    .getDaoManagerMap().get(groupName)
                    .queryForId(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        switch (statisticType) {
            case "gamesPlayed":
                result = String.valueOf(profile.getGamesPlayed());
                break;
            case "bedsBroken":
                result = String.valueOf(profile.getBedsBroken());
                break;
            case "bedsLost":
                result = String.valueOf(profile.getBedsLost());
                break;
            case "kills":
                result = String.valueOf(profile.getKills());
                break;
            case "deaths":
                result = String.valueOf(profile.getDeaths());
                break;
            case "finalKills":
                result = String.valueOf(profile.getFinalKills());
                break;
            case "wins":
                result = String.valueOf(profile.getWins());
                break;
            case "losses":
                result = String.valueOf(profile.getLosses());
                break;
            case "winstreak":
                result = String.valueOf(profile.getWinstreak());
                break;
            case "highestWinstreak":
                result = String.valueOf(profile.getHighestWinstreak());
                break;
            default:
                result = "INVALID_PLACEHOLDER";
                break;
        }

        return result;
    }
}
