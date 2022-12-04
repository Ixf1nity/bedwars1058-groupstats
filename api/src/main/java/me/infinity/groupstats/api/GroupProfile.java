package me.infinity.groupstats.api;

import java.util.UUID;
import lombok.Data;

@Data
public class GroupProfile {

  private UUID uniqueId;
  private int gamesPlayed, bedsBroken, bedsLost, kills, deaths, finalKills, finalDeaths, wins, losses, winstreak, highestWinstreak;
  private double kdr = (double) kills / deaths;
  private double fkdr = (double) finalKills / finalDeaths;
  private double bblr = (double) bedsBroken / bedsLost;
  private double wlr = (double) wins / losses;

  public GroupProfile() {
  }

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
    this.kdr = 0.0;
    this.fkdr = 0.0;
    this.bblr = 0.0;
    this.wlr = 0.0;
  }

}


