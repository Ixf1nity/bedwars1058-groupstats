package me.infinity.groupstats.core.listener;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.player.PlayerLeaveArenaEvent;
import java.util.UUID;
import lombok.SneakyThrows;
import me.infinity.groupstats.api.GroupNode;
import me.infinity.groupstats.core.GroupProfile;
import me.infinity.groupstats.core.GroupStatsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GroupStatsListener implements Listener {

  private final GroupStatsPlugin instance;
  private final BedWars bedWars;

  public GroupStatsListener(GroupStatsPlugin instance) {
    this.instance = instance;
    this.bedWars = instance.getServer().getServicesManager().getRegistration(BedWars.class)
        .getProvider();
    this.instance.getServer().getPluginManager().registerEvents(this, instance);
  }

  @EventHandler
  public void onGameStart(GameStateChangeEvent event) {
    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
      if (!(event.getNewState() == GameState.playing)) {
        return;
      }
      if (event.getArena().getGroup() == null) {
        instance.getLogger().warning(
            "The arena '" + event.getArena().getArenaName()
                + "' doesn't have any group allotted to it. GroupStats won't work due to this.");
        return;
      }

      event.getArena().getPlayers().forEach(player -> {
        instance.getGroupManager().getGroupProfileCache().get(player.getUniqueId())
            .getGroupStatistics().putIfAbsent(event.getArena().getGroup(), new GroupNode());
      });
    }, 20L);
  }

  @EventHandler
  @SneakyThrows
  public void onBedBreak(PlayerBedBreakEvent event) {
    if (event.getArena().getGroup().equals("Default")) {
      instance.getLogger().warning(
          "The arena '" + event.getArena().getArenaName()
              + "' doesn't have any group allotted to it. GroupStats won't work due to this.");
      return;
    }

    GroupProfile groupProfile = instance.getGroupManager().getGroupProfileCache()
        .get(event.getPlayer().getUniqueId());
    GroupNode groupNode = groupProfile.getGroupStatistics().get(event.getArena().getGroup());
    groupNode.setBedsBroken(groupNode.getBedsBroken() + 1);

    event.getVictimTeam().getMembers().forEach(player -> {
      GroupProfile victim = instance.getGroupManager().getGroupProfileCache()
          .get(player.getUniqueId());
      GroupNode victimGroupNode = victim.getGroupStatistics().get(event.getArena().getGroup());
      victimGroupNode.setBedsBroken(groupNode.getBedsLost() + 1);
    });
  }

  @EventHandler
  @SneakyThrows
  public void onPlayerKill(PlayerKillEvent event) {
    if (event.getArena().getGroup().equals("Default")) {
      instance.getLogger().warning(
          "The arena '" + event.getArena().getArenaName()
              + "' doesn't have any group allotted to it. GroupStats won't work due to this.");
      return;
    }
    GroupNode victimStats = instance.getGroupManager().getGroupProfileCache()
        .get(event.getVictim().getUniqueId()).getGroupStatistics().get(event.getArena().getGroup());

    GroupNode killerStats = !event.getVictim().equals(event.getKiller()) ?
        (event.getKiller() == null ? null : instance.getGroupManager().getGroupProfileCache()
            .get(event.getKiller().getUniqueId()).getGroupStatistics()
            .get(event.getArena().getGroup())) : null;

    if (event.getCause().isFinalKill()) {
      victimStats.setWinstreak(0);
      victimStats.setFinalDeaths(victimStats.getFinalDeaths() + 1);
      victimStats.setLosses(victimStats.getLosses() + 1);
      victimStats.setGamesPlayed(victimStats.getGamesPlayed() + 1);
      if (killerStats != null) {
        killerStats.setFinalKills(killerStats.getFinalKills() + 1);
      }
    } else {
      victimStats.setDeaths(victimStats.getDeaths() + 1);
      if (killerStats != null) {
        killerStats.setKills(killerStats.getKills() + 1);
      }
    }
  }

  @EventHandler
  @SneakyThrows
  public void onGameEnd(GameEndEvent event) {
    if (event.getArena().getGroup().equals("Default")) {
      instance.getLogger().warning(
          "The arena '" + event.getArena().getArenaName()
              + "' doesn't have any group allotted to it. GroupStats won't work due to this.");
      return;
    }
    for (UUID winner : event.getWinners()) {
      Player player = Bukkit.getPlayer(winner);
      if (player == null) {
        continue;
      }
      if (!player.isOnline()) {
        continue;
      }

      GroupProfile groupProfile = instance.getGroupManager().getGroupProfileCache().get(winner);
      GroupNode groupNode = groupProfile.getGroupStatistics().get(event.getArena().getGroup());
      groupNode.setWins(groupNode.getWins() + 1);
      groupNode.setWinstreak(groupNode.getWinstreak() + 1);

      if (groupNode.getHighestWinstreak() < groupNode.getWinstreak()) {
        groupNode.setHighestWinstreak(groupNode.getWinstreak());
      }

      IArena arena = bedWars.getArenaUtil().getArenaByPlayer(player);
      if (arena != null && arena.equals(event.getArena())) {
        groupNode.setGamesPlayed(groupNode.getGamesPlayed() + 1);
      }

      instance.getDatabaseManager().getHikariExecutor().execute(() -> {
        instance.getGroupManager().save(groupProfile);
      });

    }
  }

  @EventHandler
  @SneakyThrows
  public void onArenaLeave(PlayerLeaveArenaEvent event) {
    if (event.getArena().getGroup().equals("Default")) {
      instance.getLogger().warning(
          "The arena '" + event.getArena().getArenaName()
              + "' doesn't have any group allotted to it. GroupStats won't work due to this.");
      return;
    }

    final Player player = event.getPlayer();
    ITeam team = event.getArena().getExTeam(player.getUniqueId());
    if (team == null) {
      return;
    }
    if (event.getArena().getStatus() == GameState.starting
        || event.getArena().getStatus() == GameState.waiting) {
      return;
    }

    GroupProfile groupProfile = instance.getGroupManager().getGroupProfileCache()
        .get(event.getPlayer().getUniqueId());
    if (groupProfile == null) {
      return;
    }
    GroupNode groupNode = groupProfile.getGroupStatistics().get(event.getArena().getGroup());
    if (event.getArena().getStatus() == GameState.playing) {
      if (event.getArena().isPlayer(player)) {
        groupNode.setFinalDeaths(groupNode.getFinalDeaths() + 1);
        groupNode.setLosses(groupNode.getLosses() + 1);
        groupNode.setWinstreak(0);
      }

      Player damager = event.getLastDamager();
      ITeam killerTeam = event.getArena().getTeam(damager);
      if (damager != null && event.getArena().isPlayer(damager) && killerTeam != null) {
        GroupProfile damagerProfile = instance.getGroupManager().getGroupProfileCache()
            .get(event.getPlayer().getUniqueId());
        GroupNode damagerStats = damagerProfile.getGroupStatistics()
            .get(event.getArena().getGroup());
        damagerStats.setFinalKills(damagerStats.getFinalKills() + 1);

        instance.getDatabaseManager().getHikariExecutor()
            .execute(() -> instance.getGroupManager().save(damagerProfile));
      }

    } else {
      Player damager = event.getLastDamager();
      ITeam killerTeam = event.getArena().getTeam(damager);
      if (event.getLastDamager() != null && event.getArena().isPlayer(damager)
          && killerTeam != null) {
        GroupNode groupProfileStats = groupProfile.getGroupStatistics()
            .get(event.getArena().getGroup());
        groupProfileStats.setDeaths(groupProfileStats.getDeaths() + 1);
        GroupNode damagerStats = instance.getGroupManager().getGroupProfileCache()
            .get(damager.getUniqueId()).getGroupStatistics().get(event.getArena().getGroup());
        damagerStats.setKills(damagerStats.getKills() + 1);
      }
    }
  }
}
