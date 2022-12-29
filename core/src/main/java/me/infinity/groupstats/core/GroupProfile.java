package me.infinity.groupstats.core;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import me.infinity.groupstats.api.GroupNode;

@Data
@DatabaseTable(tableName = "bedwars-groupstats")
public class GroupProfile {

  private Map<String, GroupNode> groupStatistics = new ConcurrentHashMap<>();

  @DatabaseField(columnName = "uniqueId", dataType = DataType.UUID, id = true)
  private UUID uniqueId;

  @DatabaseField(columnName = "data", dataType = DataType.LONG_STRING)
  private String data;

  public GroupProfile() {
  }

  public GroupProfile(UUID uniqueId) {
    this.uniqueId = uniqueId;
    this.data = GroupStatsPlugin.GSON.toJson(groupStatistics);
  }
}
