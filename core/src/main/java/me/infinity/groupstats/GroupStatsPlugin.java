package me.infinity.groupstats;

import com.andrei1058.bedwars.api.BedWars;
import lombok.Getter;
import lombok.SneakyThrows;
import me.infinity.groupstats.api.GroupStatsAPI;
import me.infinity.groupstats.factory.DatabaseFactory;
import me.infinity.groupstats.profile.GroupProfile;
import me.infinity.groupstats.util.GroupStatsExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class GroupStatsPlugin extends JavaPlugin {

    @Getter
    private static GroupStatsPlugin instance;

    private BedWars bedWarsAPI;
    private DatabaseFactory databaseFactory;
    private boolean isDisabling;

    @Override
    @SneakyThrows
    public void onEnable() {
        instance = this;
        this.checkDepends("BedWars1058", "PlaceholderAPI");
        this.isDisabling = false;

        this.saveDefaultConfig();
        new Metrics(this, 16815);

        this.bedWarsAPI = this.getServer().getServicesManager().getRegistration(BedWars.class).getProvider();
        this.databaseFactory = new DatabaseFactory(this);
        new GroupStatsExpansion().register();

        this.getServer().getServicesManager().register(GroupStatsAPI.class, new GroupStatsAPI() {
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
        }, this, ServicePriority.Low);
    }

    @Override
    public void onDisable() {
        this.isDisabling = true;
        this.databaseFactory.closeDatabase();
    }

    public void checkDepends(String... plugins) {
        for (String plugin : plugins) {
            if (Bukkit.getPluginManager().getPlugin(plugin).isEnabled()) continue;
            this.getLogger().severe("");
            this.getLogger().severe(plugin + " not found, its an important dependency of this plugin. Disabling...");
            this.getLogger().severe("");
            Bukkit.getPluginManager().disablePlugin(this);
            break;
        }
    }
}
