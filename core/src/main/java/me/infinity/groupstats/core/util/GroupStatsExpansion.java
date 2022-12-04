package me.infinity.groupstats.core.util;

import java.sql.SQLException;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.api.GroupProfile;
import me.infinity.groupstats.core.GroupStatsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupStatsExpansion extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {
    return "bw1058_groupstats";
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
    String groupName = args[0]; // %bw1058_groupstats_Solo_kills% --- 'Solo'
    String statisticType = args[1]; // %bw1058_groupstats_Solo_kills% --- 'kills'

    GroupProfile profile;
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
      case "finalDeaths":
        result = String.valueOf(profile.getFinalDeaths());
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
      case "kdr":
        result = String.valueOf(profile.getKdr());
        break;
      case "fkdr":
        result = String.valueOf(profile.getFkdr());
        break;
      case "bblr":
        result = String.valueOf(profile.getBblr());
        break;
      case "wlr":
        result = String.valueOf(profile.getWlr());
        break;
      default:
        result = "INVALID_PLACEHOLDER";
        break;
    }

    return result;
  }
}
