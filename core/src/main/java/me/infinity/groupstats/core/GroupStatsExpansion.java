package me.infinity.groupstats.core;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.api.GroupNode;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class GroupStatsExpansion extends PlaceholderExpansion {

  private final GroupStatsPlugin instance;

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
    return instance.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    if (player == null) {
      return null;
    }

    String result;
    String[] args = params.split("_");
    if (args[0] == null || args[1] == null) {
      return "INVALID_PLACEHOLDER";
    }
    String groupName = args[0];
    String statisticType = args[1];

    GroupProfile profile = instance.getGroupManager().fetchUnsafe(player.getUniqueId());
    if (profile == null) {
      return "PLAYER_DATA_NOT_FOUND";
    }

//    if (groupName.equals("overall")) {
//      switch (statisticType) {
//        case "gamesPlayed":
//          result = String.valueOf(profile.getOverallStats().getGamesPlayed());
//          break;
//        case "bedsBroken":
//          result = String.valueOf(profile.getOverallStats().getBedsBroken());
//          break;
//        case "bedsLost":
//          result = String.valueOf(profile.getOverallStats().getBedsLost());
//          break;
//        case "kills":
//          result = String.valueOf(profile.getOverallStats().getKills());
//          break;
//        case "deaths":
//          result = String.valueOf(profile.getOverallStats().getDeaths());
//          break;
//        case "finalKills":
//          result = String.valueOf(profile.getOverallStats().getFinalKills());
//          break;
//        case "finalDeaths":
//          result = String.valueOf(profile.getOverallStats().getFinalDeaths());
//          break;
//        case "wins":
//          result = String.valueOf(profile.getOverallStats().getWins());
//          break;
//        case "losses":
//          result = String.valueOf(profile.getOverallStats().getLosses());
//          break;
//        case "winstreak":
//          result = String.valueOf(profile.getOverallStats().getWinstreak());
//          break;
//        case "highestWinstreak":
//          result = String.valueOf(profile.getOverallStats().getHighestWinstreak());
//          break;
//        case "kdr":
//          result = String.valueOf(profile.getOverallStats().getKdr());
//          break;
//        case "fkdr":
//          result = String.valueOf(profile.getOverallStats().getFkdr());
//          break;
//        case "bblr":
//          result = String.valueOf(profile.getOverallStats().getBblr());
//          break;
//        case "wlr":
//          result = String.valueOf(profile.getOverallStats().getWlr());
//          break;
//
//      }
//    }

    GroupNode groupNode = profile.toGroupStatistics().get(groupName);

    switch (statisticType) {
      case "gamesPlayed":
        result = String.valueOf(groupNode.getGamesPlayed());
        break;
      case "bedsBroken":
        result = String.valueOf(groupNode.getBedsBroken());
        break;
      case "bedsLost":
        result = String.valueOf(groupNode.getBedsLost());
        break;
      case "kills":
        result = String.valueOf(groupNode.getKills());
        break;
      case "deaths":
        result = String.valueOf(groupNode.getDeaths());
        break;
      case "finalKills":
        result = String.valueOf(groupNode.getFinalKills());
        break;
      case "finalDeaths":
        result = String.valueOf(groupNode.getFinalDeaths());
        break;
      case "wins":
        result = String.valueOf(groupNode.getWins());
        break;
      case "losses":
        result = String.valueOf(groupNode.getLosses());
        break;
      case "winstreak":
        result = String.valueOf(groupNode.getWinstreak());
        break;
      case "highestWinstreak":
        result = String.valueOf(groupNode.getHighestWinstreak());
        break;
      case "kdr":
        result = String.valueOf(groupNode.getKdr());
        break;
      case "fkdr":
        result = String.valueOf(groupNode.getFkdr());
        break;
      case "bblr":
        result = String.valueOf(groupNode.getBblr());
        break;
      case "wlr":
        result = String.valueOf(groupNode.getWlr());
        break;
      default:
        result = "INVAILD_STATISTIC";
        break;
    }
    return result;
  }

//  @Override
//  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
//    String result;
//    String[] args = params.split("_");
//    if (args[0] == null|| args[1] == null) {
//      return "INVALID_PLACEHOLDER";
//    }
//    String groupName = args[0];
//    String statisticType = args[1];
//
//    GroupProfile profile = instance.getGroupManager().fetchUnsafe(player.getUniqueId());
//    if (profile == null) {
//      return "PLAYER_DATA_NOT_FOUND";
//    }
//    if (player.isOnline())
//
//  }
}
