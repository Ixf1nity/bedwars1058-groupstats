package me.infinity.groupstats.api;

import me.infinity.groupstats.profile.GroupProfile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GroupStatsAPI {

    Optional<GroupProfile> getGroupProfile(String group, UUID playerUUID);

    boolean deleteGroupProfile(String group, UUID playerUUID);
    boolean saveGroupProfile(String group, GroupProfile groupProfile);

    boolean deleteGroupProfiles(String group);

    Optional<Map<UUID, GroupProfile>> getGroupProfiles(String group);
    Optional<Map<String, Map<UUID, GroupProfile>>> getGroupProfiles();

}
