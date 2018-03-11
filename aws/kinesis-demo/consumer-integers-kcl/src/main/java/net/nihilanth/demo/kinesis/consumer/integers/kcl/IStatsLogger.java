package net.nihilanth.demo.kinesis.consumer.integers.kcl;

/**
 *
 */
public interface IStatsLogger
{
    void addStatsProvider(IStatsProvider provider);

    void removeStatsProvider(IStatsProvider provider);
}
