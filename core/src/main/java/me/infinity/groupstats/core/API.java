package me.infinity.groupstats.core;

import me.infinity.groupstats.api.GroupNode;
import me.infinity.groupstats.api.GroupStatsAPI;
import org.bukkit.plugin.ServicePriority;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class API implements GroupStatsAPI {

    private final GroupStatsPlugin instance;

    public API(GroupStatsPlugin instance) {
        this.instance = instance;
        this.instance.getServer().getServicesManager().register(GroupStatsAPI.class, this, instance, ServicePriority.Normal);
    }

    @Override
    public Map<String, GroupNode> getPlayerStatisticsMap(UUID uuid, boolean cache) {
        Map<String, GroupNode> result;
        if (cache) {
            Optional<GroupProfile> groupProfileOptional = Optional.ofNullable(instance.getGroupManager().getGroupProfileCache().get(uuid));
            result = groupProfileOptional.map(GroupProfile::getGroupStatistics).orElse(null);
        } else {
            Optional<GroupProfile> groupProfileOptional = Optional.ofNullable(instance.getGroupManager().fetchUnsafe(uuid));
            result = groupProfileOptional.map(GroupProfile::getGroupStatistics).orElse(null);
        }
        return result;
    }
}
