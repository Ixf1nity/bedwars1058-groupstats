package me.infinity.groupstats.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.infinity.groupstats.api.GroupNode;
import me.infinity.groupstats.core.manager.DatabaseManager;
import me.infinity.groupstats.core.manager.GroupManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class GroupStatsPlugin extends JavaPlugin implements CommandExecutor {

  private boolean startupCompleted = false;
  private boolean bw1058 = false;
  public static final Gson GSON = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation()
      .disableHtmlEscaping()
      .create();

  public static final Type STATISTIC_MAP_TYPE = new TypeToken<ConcurrentHashMap<String, GroupNode>>() {
  }.getType();

  private DatabaseManager databaseManager;
  private GroupManager groupManager;

  @Override
  public void onEnable() {
    this.saveDefaultConfig();

    final PluginManager pluginManager = this.getServer().getPluginManager();
    if (pluginManager.getPlugin("BedWars1058") == null) {
      if (pluginManager.getPlugin("BedWarsProxy") == null) {
        this.getLogger().severe("BedWars1058 or BedWarsProxy not found, disabling...");
        this.setEnabled(false);
        return;
      } else {
        this.getLogger().info("BedWarsProxy found, using it as a datastore.");
      }
    } else {
      this.getLogger().info("BedWars1058 found, activating standalone mode...");
      this.bw1058 = true;
    }

    this.getLogger().info("Loading the plugin, please wait...");

    this.databaseManager = new DatabaseManager(this);
    this.groupManager = new GroupManager(this);
    new GroupStatsExpansion(this).register();

    this.getLogger().info("Loaded the plugin successfully.");
  }

  @Override
  public void onDisable() {
    this.getLogger().info("Disabling the plugin, please wait...");
    this.groupManager.saveAllAsync();
    this.databaseManager.closeDatabase();
    this.getLogger().info("Plugin disabled successfully.");
  }
}
