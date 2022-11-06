package me.infinity.groupstats;

import lombok.RequiredArgsConstructor;
import me.infinity.groupstats.api.IGroupStatsAPI;
import me.infinity.groupstats.factory.DatabaseFactory;
import me.infinity.groupstats.profile.GroupProfile;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class GroupStatsAPI implements IGroupStatsAPI {

    private final DatabaseFactory databaseFactory;

    @Override
    public Optional<GroupProfile> getGroupProfile(String group, UUID playerUUID) {
        return Optional.ofNullable(databaseFactory.getGroupProfileFactory().getCache().get(group).get(playerUUID));
    }

    @Override
    public boolean deleteGroupProfile(String group, UUID playerUUID) {
        try {
            databaseFactory.getGroupProfileFactory().getDaoManagerMap().get(group).deleteById(playerUUID);
            databaseFactory.getGroupProfileFactory().getCache().get(group).remove(playerUUID);
        } catch (SQLException e) {
            // problem occurred
            return false;
        }
        // No problems
        return true;
    }

    @Override
    public boolean saveGroupProfile(String group, GroupProfile groupProfile) {
        try {
            databaseFactory.getGroupProfileFactory().getDaoManagerMap().get(group).update(groupProfile);
            if (databaseFactory.getGroupProfileFactory().getCache().get(group).containsKey(groupProfile.getUniqueId())) {
                databaseFactory.getGroupProfileFactory().getCache().get(group).replace(groupProfile.getUniqueId(), groupProfile);
            }
        } catch (SQLException e) {
            // problem occurred
            return false;
        }
        // No problems
        return true;
    }

    @Override
    public boolean deleteGroupProfiles(String group) {
        try {
            for (GroupProfile groupProfile : databaseFactory.getGroupProfileFactory().getDaoManagerMap().get(group).queryForAll()) {
                databaseFactory.getGroupProfileFactory().getDaoManagerMap().get(group).delete(groupProfile);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public Optional<Map<UUID, GroupProfile>> getGroupProfiles(String group) {
        return Optional.ofNullable(databaseFactory.getGroupProfileFactory().getCache().get(group));
    }

    @Override
    public Optional<Map<String, Map<UUID, GroupProfile>>> getGroupProfiles() {
        return Optional.ofNullable(databaseFactory.getGroupProfileFactory().getCache());
    }
}
