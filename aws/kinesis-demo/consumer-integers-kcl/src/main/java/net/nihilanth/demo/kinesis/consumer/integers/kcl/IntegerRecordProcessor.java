package net.nihilanth.demo.kinesis.consumer.integers.kcl;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class IntegerRecordProcessor implements IRecordProcessor, IStatsProvider
{
    public static final Logger LOG = LoggerFactory.getLogger(IntegerRecordProcessor.class);

    private final IStatsLogger statsLogger;
    private final AtomicLong recordsCount = new AtomicLong(0);
    private final AtomicLong bytesCount = new AtomicLong(0);

    private final String appName;
    private final String workerId;
    private String shardId;

    public IntegerRecordProcessor(final String appName, final String workerId, final IStatsLogger statsLogger)
    {
        this.appName = appName;
        this.workerId = workerId;
        this.statsLogger = statsLogger;
    }

    @Override
    public void initialize(InitializationInput initializationInput)
    {
        shardId = initializationInput.getShardId();
        statsLogger.addStatsProvider(this);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput)
    {
        final List<Record> records = processRecordsInput.getRecords();
        long sum = 0;
        for (Record r: records) {
            recordsCount.incrementAndGet();
            final ByteBuffer data = r.getData();
            bytesCount.addAndGet(data.limit() - data.position());
            long val = Utils.extractNumber(data);
            sum += val;
        }
        try {
            processRecordsInput.getCheckpointer().checkpoint();
        } catch (InvalidStateException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ShutdownException e) {
            e.printStackTrace();
            System.exit(1);
        }

        LOG.info(String.format("%s:%s:%s: sum=%d", appName, workerId, shardId, sum));
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput)
    {
        LOG.info(String.format("%s:%s:%s: Shutting down: %s", appName, workerId, shardId, shutdownInput.getShutdownReason().toString()));
        statsLogger.removeStatsProvider(this);
    }

    @Override
    public String getName()
    {
        return shardId;
    }

    @Override
    public long getRecordsAndReset()
    {
        return recordsCount.getAndSet(0l);
    }

    @Override
    public long getBytesAndReset()
    {
        return bytesCount.getAndSet(0l);
    }
}
