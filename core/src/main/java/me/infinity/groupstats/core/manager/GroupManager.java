package me.infinity.groupstats.core.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.SneakyThrows;
import me.infinity.groupstats.core.GroupProfile;
import me.infinity.groupstats.core.GroupStatsPlugin;
import me.infinity.groupstats.core.listener.GroupStatsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

@Getter
public class GroupManager implements Listener {

  private final GroupStatsPlugin instance;
  private final Dao<GroupProfile, UUID> groupProfiles;
  private Map<UUID, GroupProfile> groupProfileCache;

  @SneakyThrows
  public GroupManager(GroupStatsPlugin instance) {
    this.instance = instance;

    this.groupProfiles = DaoManager.createDao(instance.getDatabaseManager().getConnectionSource(),
        GroupProfile.class);
    TableUtils.createTableIfNotExists(instance.getDatabaseManager().getConnectionSource(),
        GroupProfile.class);

    if (instance.isBw1058()) {
      this.groupProfileCache = new ConcurrentHashMap<>();
      instance.getServer().getScheduler()
          .runTaskTimer(instance, new GroupUpdateTask(this), 20 * 60, 20 * 60 * 5);
      instance.getServer().getPluginManager().registerEvents(this, instance);
      new GroupStatsListener(instance);
    }
  }

  @SneakyThrows
  public GroupProfile fetchLoad(UUID uniqueId) {
    Optional<GroupProfile> optionalGroupProfile = Optional.ofNullable(
        groupProfiles.queryForId(uniqueId));
    if (optionalGroupProfile.isPresent()) {
      return optionalGroupProfile.get();
    }
    GroupProfile profile = new GroupProfile(uniqueId);
    groupProfiles.create(profile);
    return profile;
  }

  @SneakyThrows
  @Nullable
  public GroupProfile fetchUnsafe(UUID uniqueId) {
    return groupProfiles.queryForId(uniqueId);
  }


  @SneakyThrows
  public void save(GroupProfile groupProfile) {
    groupProfile.setData(GroupStatsPlugin.GSON.toJson(groupProfile.getGroupStatistics()));
    groupProfiles.update(groupProfile);
  }

  public void saveAllAsync() {
    instance.getDatabaseManager().getHikariExecutor()
        .execute(() -> this.groupProfileCache.values().forEach(this::save));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    instance.getDatabaseManager().getHikariExecutor().execute(() -> {
      groupProfileCache.putIfAbsent(event.getPlayer().getUniqueId(),
          this.fetchLoad(event.getPlayer().getUniqueId()));
    });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    instance.getDatabaseManager().getHikariExecutor().execute(() -> {
      this.save(groupProfileCache.get(event.getPlayer().getUniqueId()));
      this.groupProfileCache.remove(event.getPlayer().getUniqueId());
    });
  }

}

