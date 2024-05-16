package me.infinity.groupstats.api;

import java.util.Map;
import java.util.UUID;

public interface GroupStatsAPI {

    Map<String, GroupNode> getPlayerStatisticsMap(UUID uuid, boolean cache);

}
