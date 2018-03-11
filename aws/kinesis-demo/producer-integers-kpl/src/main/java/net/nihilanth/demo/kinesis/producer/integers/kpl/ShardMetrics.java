package net.nihilanth.demo.kinesis.producer.integers.kpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ShardMetrics
{
    private final int intervalSeconds;
    private final Map<String, Double> shardToKinesisRecordsPutPerSec = new HashMap<>();
    private final Map<String, Double> shardToKinesisRecordsDataPutBytesPerSec = new HashMap<>();
    private final Map<String, Double> shardToErrorsPerSec = new HashMap<>();

    public ShardMetrics(int intervalSeconds)
    {
        this.intervalSeconds = intervalSeconds;
    }

    public void setShardKinesisRecordsPut(String shardName, double val)
    {
        this.shardToKinesisRecordsPutPerSec.put(shardName, val / intervalSeconds);
    }

    public void setShardKinesisRecordsDataPut(String shardName, double val)
    {
        this.shardToKinesisRecordsDataPutBytesPerSec.put(shardName, val / intervalSeconds);
    }

    public void setShardKinesisErrors(String shardName, double val)
    {
        this.shardToErrorsPerSec.put(shardName, val / intervalSeconds);
    }

    public Set<String> getShardNames()
    {
        final Set<String> ret = new HashSet<>(shardToErrorsPerSec.keySet());
        ret.addAll(shardToKinesisRecordsDataPutBytesPerSec.keySet());
        ret.addAll(shardToKinesisRecordsPutPerSec.keySet());

        return ret;
    }

    public ShardMetric getShardMetic(String shardName)
    {
        double recordsPerSec = shardToKinesisRecordsPutPerSec.getOrDefault(shardName, Double.NaN);
        double bytesPerSec = shardToKinesisRecordsDataPutBytesPerSec.getOrDefault(shardName, Double.NaN);
        double errorsPerSec = shardToErrorsPerSec.getOrDefault(shardName, Double.NaN);

        return new ShardMetric(shardName, errorsPerSec, recordsPerSec, bytesPerSec);
    }

    class ShardMetric {
        private final String shardName;
        private final double errorsPerSec;
        private final double recordPerSec;
        private final double bytesPerSec;

        public ShardMetric(String shardName, double errorsPerSec, double recordPerSec, double bytesPerSec)
        {
            this.shardName = shardName;
            this.errorsPerSec = errorsPerSec;
            this.recordPerSec = recordPerSec;
            this.bytesPerSec = bytesPerSec;
        }

        @Override
        public String toString()
        {
            return String.format("%20s: records/s=%.2f KB/s=%.2f errors/sec=%.2f",
                          this.shardName,
                          this.recordPerSec,
                          this.bytesPerSec / 1024,
                          this.errorsPerSec);
        }
    }
}