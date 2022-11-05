package me.infinity.groupstats.database.profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupProfileTask implements Runnable {

    private final GroupProfileFactory groupProfileFactory;

    @Override
    public void run() {
        if (groupProfileFactory.getDatabaseFactory().getInstance().isDisabling()) return;
        if (groupProfileFactory.getCache().isEmpty()) return;
        if (groupProfileFactory.getDatabaseFactory().getInstance().getServer().getOnlinePlayers().isEmpty()) return;
        this.groupProfileFactory.saveAll();
    }
}
