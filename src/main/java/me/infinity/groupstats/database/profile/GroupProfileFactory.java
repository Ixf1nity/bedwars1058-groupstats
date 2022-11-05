package me.infinity.groupstats.database.profile;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerJoinArenaEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import me.infinity.groupstats.database.DatabaseFactory;
import me.infinity.groupstats.util.GroupStatsTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GroupProfileFactory implements Listener {

    private final DatabaseFactory databaseFactory;
    private final Map<String, Dao<GroupProfile, UUID>> daoManagerMap = new ConcurrentHashMap<>();
    private final Map<String, Map<UUID, GroupProfile>> cache = new ConcurrentHashMap<>();

    public GroupProfileFactory(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
        this.databaseFactory.getInstance().getServer().getPluginManager().registerEvents(this, databaseFactory.getInstance());
        this.databaseFactory.getInstance().getServer().getScheduler().runTaskTimer(databaseFactory.getInstance(), new GroupProfileTask(this), 20 * 20, 20 * 60 * 5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Arrays.asList("Games Played : %bedwars1058_group_stats_Solo_gamesplayed%",
                "Beds Broken : %bedwars1058_group_stats_Solo_bedsbroken%",
                "Kills : %bedwars1058_group_stats_Solo_kills%",
                "Deaths : %bedwars1058_group_stats_Solo_deaths%",
                "Final Kills : %bedwars1058_group_stats_Solo_finalkills%",
                "Final Deaths : %bedwars1058_group_stats_Solo_finaldeaths%",
                "Wins : %bedwars1058_group_stats_Solo_wins%",
                "Losses : %bedwars1058_group_stats_Solo_losses%",
                "Winstreak : %bedwars1058_group_stats_Solo_winstreak%",
                "Highest Winstreak : %bedwars1058_group_stats_Solo_highestwinstreak%",
                "KDR : %bedwars1058_group_stats_Solo_kdr%",
                "Final KDR : %bedwars1058_group_stats_Solo_finalkdr%").forEach(string -> {
                    event.getPlayer().sendMessage(PlaceholderAPI.setPlaceholders(event.getPlayer(), string));
        });
    }

    @EventHandler
    @SneakyThrows
    public void onArenaJoin(PlayerJoinArenaEvent event) {
        if (event.getArena().getGroup() == null) {
            databaseFactory.getInstance().getLogger().warning("The arena '" + event.getArena().getArenaName() + "' doesn't have any group allotted to it. GroupStats won't be affected due to this.");
            return;
        }
        TableUtils.createTableIfNotExists(
                databaseFactory.getInstance().getDatabaseFactory().getConnectionSource(),
                GroupStatsTable.getDatabaseTableConfig(event.getArena().getGroup())
        );
        daoManagerMap.putIfAbsent(
                event.getArena().getGroup(),
                DaoManager.createDao(databaseFactory.getInstance().getDatabaseFactory().getConnectionSource(),
                        GroupStatsTable.getDatabaseTableConfig(event.getArena().getGroup()))
        );
        cache.putIfAbsent(event.getArena().getGroup(), new ConcurrentHashMap<>());
        cache.get(event.getArena().getGroup()).put(event.getPlayer().getUniqueId(), daoManagerMap.get(event.getArena().getGroup()).createIfNotExists(new GroupProfile(event.getPlayer().getUniqueId())));
    }

    @EventHandler
    @SneakyThrows
    public void onBedBreak(PlayerBedBreakEvent event) {
        if (event.getArena().getGroup() == null) {
            databaseFactory.getInstance().getLogger().warning("The arena '" + event.getArena().getArenaName() + "' doesn't have any group allotted to it. GroupStats won't be affected due to this.");
            return;
        }

        GroupProfile groupProfile = cache.get(event.getArena().getGroup()).get(event.getPlayer().getUniqueId());
        groupProfile.setBedsBroken(groupProfile.getBedsBroken() + 1);

        event.getVictimTeam().getMembers().forEach(player -> {
            GroupProfile groupProfile1 = cache.get(event.getArena().getGroup()).get(player.getUniqueId());
            groupProfile1.setBedsLost(groupProfile1.getBedsLost() + 1);
        });
    }

    @EventHandler
    @SneakyThrows
    public void onPlayerKill(PlayerKillEvent event) {
        if (event.getArena().getGroup() == null) {
            databaseFactory.getInstance().getLogger().warning("The arena '" + event.getArena().getArenaName() + "' doesn't have any group allotted to it.     GroupStats won't be affected due to this.");
            return;
        }

        GroupProfile groupProfile = cache.get(event.getArena().getGroup()).get(event.getVictim().getUniqueId());
        GroupProfile groupProfile1 = !event.getVictim().equals(event.getKiller()) ?
                (event.getKiller() == null ? null : cache.get(event.getArena().getGroup()).get(event.getKiller().getUniqueId())) : null;

        if (event.getCause().isFinalKill()) {
            groupProfile.setFinalDeaths(groupProfile.getFinalKills() + 1);
            groupProfile.setLosses(groupProfile.getLosses() + 1);
            groupProfile.setGamesPlayed(groupProfile.getGamesPlayed() + 1);
            if (groupProfile1 != null) groupProfile1.setFinalKills(groupProfile1.getFinalKills() + 1);
        } else {
            groupProfile.setDeaths(groupProfile.getDeaths() + 1);
            if (groupProfile1 != null) groupProfile1.setKills(groupProfile1.getKills() + 1);
        }
    }

    @EventHandler
    @SneakyThrows
    public void onGameEnd(GameEndEvent event) {
        if (event.getArena().getGroup() == null) {
            databaseFactory.getInstance().getLogger().warning("The arena '" + event.getArena().getArenaName() + "' doesn't have any group allotted to it.     GroupStats won't be affected due to this.");
            return;
        }
        for (UUID winner : event.getWinners()) {
            Player player = Bukkit.getPlayer(winner);
            if (player == null) continue;
            if (!player.isOnline()) continue;

            GroupProfile groupProfile = cache.get(event.getArena().getGroup()).get(winner);
            groupProfile.setWins(groupProfile.getWins() + 1);
            groupProfile.setWinstreak(groupProfile.getWinstreak() + 1);

            if (groupProfile.getHighestWinstreak() < groupProfile.getWinstreak()) {
                groupProfile.setHighestWinstreak(groupProfile.getWinstreak());
            }

            IArena arena = databaseFactory.getInstance().getBedWarsAPI().getArenaUtil().getArenaByPlayer(player);
            if (arena != null && arena.equals(event.getArena())) {
                groupProfile.setGamesPlayed(groupProfile.getGamesPlayed() + 1);
            }

            daoManagerMap.get(event.getArena().getGroup()).update(cache.get(event.getArena().getGroup()).get(winner));
        }
    }

    @EventHandler
    @SneakyThrows
    public void onArenaLeave(PlayerLeaveArenaEvent event) {
        if (event.getArena().getGroup() == null) {
            databaseFactory.getInstance().getLogger().warning("The arena '" + event.getArena().getArenaName() + "' doesn't have any group allotted to it.     GroupStats won't be affected due to this.");
            return;
        }

        final Player player = event.getPlayer();
        ITeam team = event.getArena().getExTeam(player.getUniqueId());
        if (team == null) return;
        if (event.getArena().getStatus() == GameState.starting || event.getArena().getStatus() == GameState.waiting)
            return;

        GroupProfile groupProfile = cache.get(event.getArena().getGroup()).get(player.getUniqueId());
        if (groupProfile == null) return;

        if (event.getArena().getStatus() == GameState.playing) {
            if (event.getArena().isPlayer(player)) {
                groupProfile.setFinalDeaths(groupProfile.getFinalDeaths() + 1);
                groupProfile.setLosses(groupProfile.getLosses() + 1);
                groupProfile.setWinstreak(0);
            }

            Player damager = event.getLastDamager();
            ITeam killerTeam = event.getArena().getTeam(damager);
            if (damager != null && event.getArena().isPlayer(damager) && killerTeam != null) {
                GroupProfile damagerStats = cache.get(event.getArena().getGroup()).get(damager.getUniqueId());
                damagerStats.setFinalKills(damagerStats.getFinalKills() + 1);
                daoManagerMap.get(event.getArena().getGroup()).update(cache.get(event.getArena().getGroup()).get(event.getPlayer().getUniqueId()));
            }

        } else {
            Player damager = event.getLastDamager();
            ITeam killerTeam = event.getArena().getTeam(damager);
            if (event.getLastDamager() != null && event.getArena().isPlayer(damager) && killerTeam != null) {
                groupProfile.setDeaths(groupProfile.getDeaths() + 1);
                GroupProfile damagerStats = cache.get(event.getArena().getGroup()).get(damager.getUniqueId());
                damagerStats.setKills(damagerStats.getKills() + 1);
            }
        }
        daoManagerMap.get(event.getArena().getGroup()).update(cache.get(event.getArena().getGroup()).get(event.getPlayer().getUniqueId()));
    }

    public void saveAll() {
        databaseFactory.getHikariExecutor().execute(() -> {
            cache.forEach(((s, uuidGroupProfileMap) -> {
                uuidGroupProfileMap.values().forEach(profile -> {
                    try {
                        daoManagerMap.get(s).update(profile);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }));
            databaseFactory.getInstance().getLogger().info("Saving player data, Might cause lag spikes for a short amount of time.");
        });
    }
}

