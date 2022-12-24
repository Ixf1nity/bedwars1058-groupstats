package me.infinity.groupstats.api;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GroupNode {

  @Expose
  private int gamesPlayed, bedsBroken, bedsLost, kills, deaths, finalKills, finalDeaths, wins, losses, winstreak, highestWinstreak;

  private double kdr = (double) kills / deaths;
  private double fkdr = (double) finalKills / finalDeaths;
  private double bblr = (double) bedsBroken / bedsLost;
  private double wlr = (double) wins / losses;

  public GroupNode() {
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