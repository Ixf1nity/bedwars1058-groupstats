package me.infinity.groupstats.core;

import com.andrei1058.bedwars.api.BedWars;
import lombok.Getter;
import lombok.SneakyThrows;
import me.infinity.groupstats.api.GroupStatsAPI;
import me.infinity.groupstats.core.factory.DatabaseFactory;
import me.infinity.groupstats.core.util.GroupStatsExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

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

        this.getServer().getServicesManager().register(GroupStatsAPI.class, new me.infinity.groupstats.core.GroupStatsAPI(databaseFactory), this, ServicePriority.Low);
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
