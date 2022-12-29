package me.infinity.groupstats.core.manager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupUpdateTask implements Runnable {

  private final GroupManager groupManager;

  @Override
  public void run() {
    groupManager.saveAllAsync();
  }
}
