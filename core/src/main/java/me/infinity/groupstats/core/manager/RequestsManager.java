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

                    JSONObject json = this.getPlayerStats(offlinePlayer);
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
        json.put("uuid", offlinePlayer.getUniqueId());
        json.put("stats", profile.getData());

        return json;
    }

}
