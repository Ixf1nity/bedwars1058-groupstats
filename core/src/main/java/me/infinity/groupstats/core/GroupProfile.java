package me.infinity.groupstats.core;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;
import lombok.Data;
import me.infinity.groupstats.api.GroupStatistics;

@Data
@DatabaseTable(tableName = "bedwars-groupstats")
public class GroupProfile {

  private GroupStatistics groupStatistics;
//  private GroupNode overallStats = GroupNode.builder()
//      .gamesPlayed(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getGamesPlayed).sum())
//      .bedsBroken(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getBedsBroken).sum())
//      .bedsLost(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getBedsLost).sum())
//      .kills(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getKills).sum())
//      .deaths(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getDeaths).sum())
//      .finalKills(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getFinalKills).sum())
//      .finalDeaths(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getFinalDeaths).sum())
//      .wins(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getWins).sum())
//      .losses(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getLosses).sum())
//      .winstreak(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getWinstreak).sum())
//      .highestWinstreak(groupStatistics.isEmpty() ? 0 : groupStatistics.values().stream().mapToInt(GroupNode::getHighestWinstreak).max().getAsInt())
//      .build();

  @DatabaseField(columnName = "uniqueId", dataType = DataType.UUID, id = true)
  private UUID uniqueId;

  @DatabaseField(columnName = "data", dataType = DataType.LONG_STRING)
  private String data;

  public GroupProfile() {
  }

  public GroupProfile(UUID uniqueId) {
    this.uniqueId = uniqueId;
    this.groupStatistics = new GroupStatistics();
    this.data = GroupStatsPlugin.GSON.toJson(groupStatistics);
  }

  public GroupStatistics toGroupStatistics() {
    return GroupStatsPlugin.GSON.fromJson(data, GroupStatistics.class);
  }
}
