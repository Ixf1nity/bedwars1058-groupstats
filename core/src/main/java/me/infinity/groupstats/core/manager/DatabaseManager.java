package me.infinity.groupstats.core.manager;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    hikariConfig.addDataSourceProperty("socketTimeout", TimeUnit.SECONDS.toMillis(30));
    hikariConfig.setMaximumPoolSize(10);
    hikariConfig.setMaxLifetime(1800000);
    hikariConfig.setMinimumIdle(10);
    hikariConfig.setKeepaliveTime(0);
    hikariConfig.setConnectionTimeout(5000);
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
    ConfigurationSection configuration = instance.getConfig()
        .getConfigurationSection("database-credentials");
    this.dbEnabled = configuration.getBoolean("enabled");
    this.address = configuration.getString("address");
    this.port = configuration.getInt("port");
    this.database = configuration.getString("database");
    this.username = configuration.getString("username");
    this.password = configuration.getString("password");
    this.ssl = configuration.getBoolean("useSSL");
  }

  public void closeDatabase() {
    this.hikariDataSource.close();
    instance.getLogger().info("Disconnected to database successfully.");
  }

}
