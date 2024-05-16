package me.infinity.groupstats.core;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.api.GroupNode;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class GroupStatsExpansion extends PlaceholderExpansion {

  private final GroupStatsPlugin instance;

  @Override
  public @NotNull String getIdentifier() {
    return "groupstats";
  }

  @Override
  public @NotNull String getAuthor() {
    return "infinity";
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
      return "0";
    }

    ConcurrentHashMap<String, GroupNode> stats = GroupStatsPlugin.GSON.fromJson(profile.getData(),
        GroupStatsPlugin.STATISTIC_MAP_TYPE);

    if (groupName.equals("overAll")) {

      switch (statisticType) {
        case "gamesPlayed":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getGamesPlayed).sum());
          break;
        case "bedsBroken":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getBedsBroken).sum());
          break;
        case "bedsLost":
          result = String.valueOf(
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getBedsLost).sum());
          break;
        case "kills":
          result = String.valueOf(
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getKills).sum());
          break;
        case "deaths":
          result = String.valueOf(
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getDeaths).sum());
          break;
        case "finalKills":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getFinalKills).sum());
          break;
        case "finalDeaths":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getFinalDeaths).sum());
          break;
        case "wins":
          result = String.valueOf(
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getWins).sum());
          break;
        case "losses":
          result = String.valueOf(
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getLosses).sum());
          break;
        case "winstreak":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getWinstreak).sum());
          break;
        case "highestWinstreak":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getHighestWinstreak).max().getAsInt());
          break;
        case "kdr":
          int k = stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getKills).sum();
          int d =
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getDeaths).sum();
          result = String.valueOf(this.getRatio(k, d));
          break;
        case "fkdr":
          int fk = stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getFinalKills).sum();
          int fd = stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getFinalDeaths).sum();
          result = String.valueOf(this.getRatio(fk, fd));
          break;
        case "bblr":
          int bb = stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getBedsBroken).sum();
          int bl =
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getBedsLost).sum();
          result = String.valueOf(this.getRatio(bb, bl));
          break;
        case "wlr":
          int w = stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getWins).sum();
          int l =
              stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getLosses).sum();
          result = String.valueOf(this.getRatio(w, l));
          break;
        default:
          result = "0";
          break;
      }
      return result;
    }

    GroupNode groupNode = stats.get(groupName);
    if (groupNode == null) {
      return "0";
    }

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
        result = String.valueOf(this.getRatio(groupNode, "kdr"));
        break;
      case "fkdr":
        result = String.valueOf(this.getRatio(groupNode, "fkdr"));
        break;
      case "bblr":
        result = String.valueOf(this.getRatio(groupNode, "bblr"));
        break;
      case "wlr":
        result = String.valueOf(this.getRatio(groupNode, "wlr"));
        break;
      default:
        result = "0";
        break;
    }
    return result;
  }

  public double getRatio(GroupNode groupNode, String type) {
    double result;
    switch (type) {
      case "kdr":
        int deaths = groupNode.getDeaths();
        if (deaths == 0) {
          deaths = 1;
        }
        result = this.getRatio(groupNode.getKills(), deaths);
        break;
      case "fkdr":
        int finalDeaths = groupNode.getFinalDeaths();
        if (finalDeaths == 0) {
          finalDeaths = 1;
        }
        result = this.getRatio(groupNode.getFinalKills(), finalDeaths);
        break;
      case "bblr":
        int bedsLost = groupNode.getBedsLost();
        if (bedsLost == 0) {
          bedsLost = 1;
        }
        result = this.getRatio(groupNode.getBedsBroken(), bedsLost);
        break;
      case "wlr":
        int losses = groupNode.getLosses();
        if (losses == 0) {
          losses = 1;
        }
        result = this.getRatio(groupNode.getWins(), losses);
        break;
      default:
        result = Double.NaN;
        break;
    }
    return result;
  }

  public double getRatio(int i1, int i2) {
    if (i2 == 0) {
      // Handle division by zero error here, e.g., return Double.NaN or throw an exception.
      return Double.NaN;
    }

    double value = (double) i1 / i2;
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
