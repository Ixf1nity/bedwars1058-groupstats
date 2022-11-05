package me.infinity.groupstats.util;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.table.DatabaseTableConfig;
import lombok.experimental.UtilityClass;
import me.infinity.groupstats.database.profile.GroupProfile;
import java.util.Arrays;

@UtilityClass
public class GroupStatsTable {

    public DatabaseTableConfig<GroupProfile> getDatabaseTableConfig(String group) {
        DatabaseFieldConfig uuid = new DatabaseFieldConfig("uniqueId");
        uuid.setDataType(DataType.UUID);
        uuid.setId(true);
        uuid.setColumnName("UUID");

        DatabaseFieldConfig gamesPlayed = new DatabaseFieldConfig("gamesPlayed");
        gamesPlayed.setDataType(DataType.INTEGER);
        gamesPlayed.setColumnName("GAMES_PLAYED");

        DatabaseFieldConfig bedsBroken = new DatabaseFieldConfig("bedsBroken");
        bedsBroken.setDataType(DataType.INTEGER);
        bedsBroken.setColumnName("BEDS_BROKEN");

        DatabaseFieldConfig bedsLost = new DatabaseFieldConfig("bedsLost");
        bedsLost.setDataType(DataType.INTEGER);
        bedsLost.setColumnName("BEDS_LOST");

        DatabaseFieldConfig kills = new DatabaseFieldConfig("kills");
        kills.setDataType(DataType.INTEGER);
        kills.setColumnName("KILLS");

        DatabaseFieldConfig deaths = new DatabaseFieldConfig("deaths");
        deaths.setDataType(DataType.INTEGER);
        deaths.setColumnName("DEATHS");

        DatabaseFieldConfig finalKills = new DatabaseFieldConfig("finalKills");
        finalKills.setDataType(DataType.INTEGER);
        finalKills.setColumnName("FINAL_KILLS");

        DatabaseFieldConfig finalDeaths = new DatabaseFieldConfig("finalDeaths");
        finalDeaths.setDataType(DataType.INTEGER);
        finalDeaths.setColumnName("FINAL_DEATHS");

        DatabaseFieldConfig wins = new DatabaseFieldConfig("wins");
        wins.setDataType(DataType.INTEGER);
        wins.setColumnName("WINS");

        DatabaseFieldConfig losses = new DatabaseFieldConfig("losses");
        losses.setDataType(DataType.INTEGER);
        losses.setColumnName("LOSSES");

        DatabaseFieldConfig winstreak = new DatabaseFieldConfig("winstreak");
        winstreak.setDataType(DataType.INTEGER);
        winstreak.setColumnName("WINSTREAK");

        DatabaseFieldConfig highestWinstreak = new DatabaseFieldConfig("highestWinstreak");
        highestWinstreak.setDataType(DataType.INTEGER);
        highestWinstreak.setColumnName("HIGHEST_WINSTREAK");

        return new DatabaseTableConfig<>(GroupProfile.class, "groupstats-" + group, Arrays.asList(uuid, gamesPlayed, bedsBroken, bedsLost, kills, deaths, finalKills, finalDeaths, wins, losses, winstreak, highestWinstreak));
    }
}
