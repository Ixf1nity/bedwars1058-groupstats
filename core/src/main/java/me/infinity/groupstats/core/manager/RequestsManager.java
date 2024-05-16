package me.infinity.groupstats.core.manager;

import me.infinity.groupstats.api.GroupNode;
import me.infinity.groupstats.core.GroupProfile;
import me.infinity.groupstats.core.GroupStatsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import spark.Spark;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestsManager {

    private final GroupStatsPlugin instance;
    private boolean apiActive;

    public RequestsManager(GroupStatsPlugin instance) {
        this.instance = instance;
        this.apiActive = this.instance.getConfig().getBoolean("api.enabled", false);
        if (apiActive) {
            enableApi();
        }
    }

    private void enableApi() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!apiActive) {
                    return;
                }
                int port = instance.getConfig().getInt("api.port");
                if (port == 0) {
                    apiActive = false;
                    return;
                }
                instance.getLogger().info("Detected that Rest API is enabled. Enabling Rest API now.");
                Spark.port(port);
                instance.getLogger().info("Set GroupStats API to listen on port " + port);
                Spark.get("/stats", (req, res) -> {
                    res.type("application/json");
                    Set<String> params = req.queryParams();
                    OfflinePlayer offlinePlayer;
                    if (params.contains("uuid")) {
                        try {
                            UUID uuid = UUID.fromString(req.queryParams("uuid"));
                            offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        } catch (IllegalArgumentException ex) {
                            return getFailJson("Invalid UUID provided");
                        }
                    } else if (params.contains("name")) {
                        String username = req.queryParams("name");
                        //noinspection deprecation
                        offlinePlayer = Bukkit.getOfflinePlayer(username);
                    } else {
                        res.status(411);
                        return getFailJson("Invalid name provided");
                    }

                    JSONObject json = getPlayerStats(offlinePlayer);
                    if (json == null) {
                        return getFailJson("Player data is empty");
                    }
                    res.status(201);
                    return json.toString();
                });
            }
        }.runTaskAsynchronously(instance);
    }

    public void onDisable() {
        if (!apiActive) {
            return;
        }
        Spark.stop();
    }

    @SuppressWarnings("unchecked")
    private JSONObject getFailJson(String reason) {
        JSONObject json = new JSONObject();
        json.put("status", "Failed");
        json.put("reason", reason);

        return json;
    }

    @SuppressWarnings("unchecked")
    private JSONObject getPlayerStats(OfflinePlayer offlinePlayer) {
        GroupProfile profile = this.instance.getGroupManager().fetchUnsafe(offlinePlayer.getUniqueId());
        if (profile == null) {
            return null;
        }

        JSONObject json = new JSONObject();
        json.put("status", "Success");
        json.put("name", offlinePlayer.getName());

        ConcurrentHashMap<String, GroupNode> stats = GroupStatsPlugin.GSON.fromJson(profile.getData(),
                GroupStatsPlugin.STATISTIC_MAP_TYPE);

        JSONObject statsObject = new JSONObject();

        //<editor-fold desc="Overall stats">
        JSONObject overallStats = new JSONObject();
        overallStats.put("games played", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getGamesPlayed).sum());
        overallStats.put("beds broken", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getBedsBroken).sum());
        overallStats.put("beds lost", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getBedsLost).sum());
        overallStats.put("kills", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getKills).sum());
        overallStats.put("deaths", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getDeaths).sum());
        overallStats.put("final kills", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getFinalKills).sum());
        overallStats.put("final deaths", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getFinalDeaths).sum());
        overallStats.put("wins", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getWins).sum());
        overallStats.put("losses", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getLosses).sum());
        overallStats.put("winstreak", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getWinstreak).sum());
        overallStats.put("highest winstreak", stats.isEmpty() ? 0
                : stats.values().stream().mapToInt(GroupNode::getHighestWinstreak).sum());
        overallStats.put("kdr", instance.getGroupStatsExpansion().getRatio(
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getKills).sum(),
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getDeaths).sum()));
        overallStats.put("fkdr", instance.getGroupStatsExpansion().getRatio(
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getFinalKills).sum(),
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getFinalDeaths).sum()));
        overallStats.put("bblr", instance.getGroupStatsExpansion().getRatio(
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getBedsBroken).sum(),
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getBedsLost).sum()));
        overallStats.put("wlr", instance.getGroupStatsExpansion().getRatio(
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getWins).sum(),
                stats.isEmpty() ? 0 : stats.values().stream().mapToInt(GroupNode::getLosses).sum()));
        statsObject.put("over-all", overallStats);
        //</editor-fold>


        //<editor-fold desc="Per group stats">
        stats.forEach((name, node) -> {
            JSONObject groupJson = new JSONObject();

            groupJson.put("games played", node.getGamesPlayed());
            groupJson.put("beds broken", node.getBedsBroken());
            groupJson.put("beds lost", node.getBedsLost());
            groupJson.put("kills", node.getKills());
            groupJson.put("deaths", node.getDeaths());
            groupJson.put("final kills", node.getFinalKills());
            groupJson.put("final deaths", node.getFinalDeaths());
            groupJson.put("wins", node.getWins());
            groupJson.put("losses", node.getLosses());
            groupJson.put("winstreak", node.getWinstreak());
            groupJson.put("highest winstreak", node.getHighestWinstreak());
            groupJson.put("kdr", instance.getGroupStatsExpansion().getRatio(node, "kdr"));
            groupJson.put("fkdr", instance.getGroupStatsExpansion().getRatio(node, "fkdr"));
            groupJson.put("bblr", instance.getGroupStatsExpansion().getRatio(node, "bblr"));
            groupJson.put("wlr", instance.getGroupStatsExpansion().getRatio(node, "wlr"));

            statsObject.put(name, groupJson);
        });
        //</editor-fold>

        json.put("stats", statsObject);

        return json;
    }

}