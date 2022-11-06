package me.infinity.groupstats.api;

import lombok.Data;

import java.util.UUID;

@Data
public class GroupProfile  {

    private UUID uniqueId;
    private int gamesPlayed, bedsBroken, bedsLost, kills, deaths, finalKills, finalDeaths, wins, losses, winstreak, highestWinstreak;

    public GroupProfile() {}

    public GroupProfile(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.gamesPlayed = 0;
        this.bedsBroken = 0;
        this.bedsLost = 0;
        this.kills = 0;
        this.deaths = 0;
        this.finalKills = 0;
        this.finalDeaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.winstreak = 0;
        this.highestWinstreak = 0;
    }

}


