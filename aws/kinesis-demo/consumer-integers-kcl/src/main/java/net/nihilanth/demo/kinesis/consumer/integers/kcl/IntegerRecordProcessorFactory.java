package net.nihilanth.demo.kinesis.consumer.integers.kcl;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

/**
 *
 */
public class IntegerRecordProcessorFactory implements IRecordProcessorFactory
{
    private final String appName;
    private final String workerId;
    private final IStatsLogger statsLogger;

    public IntegerRecordProcessorFactory(String appName, String workerId, IStatsLogger statsLogger)
    {
        this.appName = appName;
        this.workerId = workerId;
        this.statsLogger = statsLogger;
    }

    @Override
    public IRecordProcessor createProcessor()
    {
        return new IntegerRecordProcessor(appName, workerId, statsLogger);
    }
}
