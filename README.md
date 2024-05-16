# BedWars1058 GroupStats Addon
 A BedWars1058 addon that adds placeholders for getting statistics of an player filtered by a Arena Group.  Supports MySQL and SQLite. 
 
## Placeholders
> ### NOTE:  Use overAll as <group> to get overall statistics. Example, %groupstats_overAll_gamesPlayed%

|Placeholder                          |Returns                      |
|-----------------------------------------------|-----------------------------|
|` %groupstats_<group>_gamesPlayed%`            |how many games the player has played.            |
|`%groupstats_<group>_bedsBroken%`            |how many beds the player has broken.            |
|`%groupstats_<group>_bedsLost%`            |how many beds the player has lost.            |
|`%groupstats_<group>_kills%`            |how many kills the player has.
|`%groupstats_<group>_deaths%`            |how many times the player has died.            |            ||`%groupstats_<group>_bedsLost%`            |how many beds the player has lost.            |
|`%groupstats_<group>_finalKills%`            |how many final kills the player has.            |
|`%groupstats_<group>_finalDeaths%`            |how many times player has been eliminated.            |
|`%groupstats_<group>_wins%`            |how many times player has been won.            |
|`%groupstats_<group>_losses%`            |how many times player has lost.            |
|`%groupstats_<group>_winstreak%`            |how many times player has won without losing.            |
|`%groupstats_<group>_highestWinstreak%`            |highest times player has won without losing.            |
|`%groupstats_<group>_kdr%`            |player's kill/death ratio.            |
|`%groupstats_<group>_fkdr%`            |player's final kill/death ratio.            |
|`%groupstats_<group>_bblr%`            |player's beds broken/lost ratio.
|`%groupstats_<group>_wlr%`            |player's win/lose ratio.            |            |

## Developer API
> ### Maven Repository
```xml
<repositories>
 <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
 </repository>
</repositories>
```
```xml
<dependencies>
 <dependency>
  <groupId>com.github.Ixf1nity</groupId>
  <artifactId>bedwars1058-groupstats</artifactId>
  <version>api-SNAPSHOT</version>
 </dependency>
</dependencies>
```


> #### If you want to use the API make sure to load your plugin after BedWars1058-GroupStats .

1. Add it as softdepend in plugin.yml: softdepend: ``[BedWars1058-GroupStats]``
2. Check if BedWars1058-GroupStats is on the server:

```java
@Override
public void onEnable() {
    //Disable if pl not found
    if (Bukkit.getPluginManager().getPlugin("BedWars1058-GroupStats") == null) {
            getLogger().severe("BedWars1058-GroupStats was not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
    }
}
```
> #### Initializing the API:
```java
GroupStatsAPI groupStatsAPI = Bukkit.getServicesManager().getRegistration(GroupStatsAPI.class).getProvider();
```
> #### Fetching player statistics: (example of fetching winstreak)
```java
// uuid perameter is the uuid of the player
// cache perameter is a boolean, if true, the stats would be fetched from the cache (realtime). If false, the stats would be fetched from the database.
// group perameter specifics which group data is demanded.
GroupStatsAPI.getPlayerStatisticsMap(uuid, cache).get(group).getWinstreak(); // retuns winstreak of specified group
```

Questions? [Join the I6y's Hub Discord Server](https://discord.gg/UTu2vRUuge)

