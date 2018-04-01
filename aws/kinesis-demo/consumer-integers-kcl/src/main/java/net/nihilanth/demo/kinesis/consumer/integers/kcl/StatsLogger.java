package net.nihilanth.demo.kinesis.consumer.integers.kcl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 */
public class StatsLogger implements Runnable, IStatsLogger
{
    public static final Logger LOG = LoggerFactory.getLogger(StatsLogger.class);

    private final String appName;
    private final String workerId;
    private final int intervalSeconds;
    private final Set<IStatsProvider> providers = new HashSet<>();

    public StatsLogger(String appName, String workerId, int intervalSeconds)
    {
        this.appName = appName;
        this.workerId = workerId;
        this.intervalSeconds = intervalSeconds;
    }

    @Override
    public synchronized void addStatsProvider(IStatsProvider provider) {
        this.providers.add(provider);
    }

    @Override
    public synchronized void removeStatsProvider(IStatsProvider provider) {
        this.providers.remove(provider);
    }

    private synchronized void printStats() {
        for (IStatsProvider provider : providers) {
            LOG.info(String.format("%s:%s:%s: UserRecords/sec=%.2f KB/s=%.2f",
                                   appName,
                                   workerId,
                                   provider.getName(),
                                   1.0 * provider.getRecordsAndReset() / intervalSeconds,
                                   1.0 * provider.getBytesAndReset() / (1024 * intervalSeconds)));
        }
    }

    @Override
    public void run()
    {
        while (true) {
            try {
                printStats();
                Thread.sleep(intervalSeconds * 1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
