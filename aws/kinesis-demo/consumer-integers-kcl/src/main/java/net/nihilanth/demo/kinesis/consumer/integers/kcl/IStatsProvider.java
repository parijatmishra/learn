package net.nihilanth.demo.kinesis.consumer.integers.kcl;

/**
 *
 */
public interface IStatsProvider
{
    public String getName();
    public long getRecordsAndReset();
    public long getBytesAndReset();
}
