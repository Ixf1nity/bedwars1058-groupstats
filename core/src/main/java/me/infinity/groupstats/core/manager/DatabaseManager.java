package me.infinity.groupstats.core.manager;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.SneakyThrows;
import me.infinity.groupstats.core.GroupStatsPlugin;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class DatabaseManager {

  private final GroupStatsPlugin instance;

  private final HikariDataSource hikariDataSource;
  private final ConnectionSource connectionSource;

  private final Executor hikariExecutor = Executors.newFixedThreadPool(8);

  private String address, database, username, password;
  private int port;
  private boolean ssl, dbEnabled;

  @SneakyThrows
  public DatabaseManager(GroupStatsPlugin instance) {
    this.instance = instance;
    this.loadCredentials();

    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
    hikariConfig.addDataSourceProperty("useUnicode", true);
    hikariConfig.addDataSourceProperty("useSSL", this.ssl);
    hikariConfig.setMaximumPoolSize(10);
    hikariConfig.setUsername(this.username);
    hikariConfig.setPassword(this.password);
    hikariConfig.setPoolName("bedwars1058-groupstats-pool");
    hikariConfig.setConnectionTestQuery("SELECT 1;");

    if (this.dbEnabled) {
      hikariConfig.setJdbcUrl(
          "jdbc:mysql://" + this.address + ":" + this.port + "/" + this.database);
      instance.getLogger().info("Starting connection to database with MySQL");
    } else {
      File database = new File(instance.getDataFolder(), "statistics.db");
      if (!database.exists()) {
        database.createNewFile();
      }
      hikariConfig.setJdbcUrl("jdbc:sqlite:" + database.getPath());
      hikariConfig.setDriverClassName("org.sqlite.JDBC");
      instance.getLogger().info("Starting connection to database with SQLite");
    }
    this.hikariDataSource = new HikariDataSource(hikariConfig);
    this.connectionSource = new DataSourceConnectionSource(hikariDataSource,
        hikariDataSource.getJdbcUrl());

    if (hikariDataSource.isRunning()) {
      instance.getLogger().info("Established connection to database successfully.");
    }

  }

  private void loadCredentials() {
    ConfigurationSection configuration = instance.getConfig().getConfigurationSection("DATABASE");
    this.dbEnabled = configuration.getBoolean("ENABLED");
    this.address = configuration.getString("ADDRESS");
    this.port = configuration.getInt("PORT");
    this.database = configuration.getString("DATABASE");
    this.username = configuration.getString("USERNAME");
    this.password = configuration.getString("PASSWORD");
    this.ssl = configuration.getBoolean("SSL");
  }

  public void closeDatabase() {
    this.hikariDataSource.close();
    instance.getLogger().info("Disconnected to database successfully.");
  }

}
