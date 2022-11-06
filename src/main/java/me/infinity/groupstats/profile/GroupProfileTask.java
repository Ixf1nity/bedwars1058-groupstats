package me.infinity.groupstats.profile;

import lombok.RequiredArgsConstructor;
import me.infinity.groupstats.factory.GroupProfileFactory;

@RequiredArgsConstructor
public class GroupProfileTask implements Runnable {

    private final GroupProfileFactory groupProfileFactory;

    @Override
    public void run() {
        if (groupProfileFactory.getDatabaseFactory().getInstance().isDisabling()) return;
        if (groupProfileFactory.getCache().isEmpty()) return;
        if (groupProfileFactory.getDatabaseFactory().getInstance().getServer().getOnlinePlayers().isEmpty()) return;

        this.groupProfileFactory.getDatabaseFactory().getInstance().getLogger().info("Saving player data, Might cause lag spikes for a short amount of time.");
        this.groupProfileFactory.saveAll();
    }
}
