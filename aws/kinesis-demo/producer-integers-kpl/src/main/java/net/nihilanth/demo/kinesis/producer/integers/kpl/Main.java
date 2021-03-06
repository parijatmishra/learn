package net.nihilanth.demo.kinesis.producer.integers.kpl;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.producer.*;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class writes data to a Kinesis stream at a fixed rate, using the Kinesis Main Library (KPL)
 * to aggregate logical records into Kinesis records and achieve high throughput.
 * <p>
 * <p>Each logical record is a string of size DATA_SIZE, containing a Long formatted in decimal, followed by
 * a space, followed by an arbitrary number of alphabetic characters to pad the string to the required size.</p>
 */
public class Main
{
    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final int DATA_SIZE;
    public static final int RECORDS_PER_SECOND;
    public static final Region KINESIS_REGION;
    public static final String KINESIS_STREAM_NAME;
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4);

    static {
        // KINESIS_REGION
        Region kinesisRegion = Regions.getCurrentRegion();
        String kinesisRegionStr = System.getenv("KINESIS_REGION");
        if (kinesisRegionStr != null) {
            try {
                kinesisRegion = Region.getRegion(Regions.fromName(kinesisRegionStr));
            } catch (IllegalArgumentException e) {
                LOG.warn(String.format("Error parsing env var KINESIS_REGION [%s]: %s. Using %s",
                        kinesisRegionStr,
                        e.getMessage(),
                        kinesisRegion.getName()));
            }
        }
        KINESIS_REGION = kinesisRegion;
        LOG.info(String.format("KINESIS_REGION: %s", KINESIS_REGION));

        // KINESIS_STREAM_NAME
        String kinesisStreamNameStr = System.getenv("KINESIS_STREAM_NAME");
        if (kinesisRegionStr == null) {
            LOG.error("FATAL: environment variable KINESIS_STREAM_NAME is required.");
            System.exit(1);
        }
        if (!streamExists(kinesisStreamNameStr, KINESIS_REGION)) {
            LOG.error(String.format("FATAL: stream %s does not exist.", kinesisStreamNameStr));
            System.exit(1);
        }
        KINESIS_STREAM_NAME = kinesisStreamNameStr;
        LOG.info(String.format("KINESIS_STREAM_NAME: %s", KINESIS_STREAM_NAME));

        // DATA_SIZE
        int dataSize = 128;
        String dataSizeStr = System.getenv("DATA_SIZE");
        if (dataSizeStr != null) {
            try {
                dataSize = Integer.parseUnsignedInt(dataSizeStr);
            } catch (NumberFormatException e) {
                LOG.warn(String.format("Error parsing env var DATA_SIZE [%s]: %s.  Using %s.",
                        dataSizeStr,
                        e.getMessage(),
                        dataSize));
            }
        }
        DATA_SIZE = dataSize;
        LOG.info(String.format("DATA_SIZE: %d bytes", DATA_SIZE));

        // RECORDS_PER_SECOND
        int rps = 1000;
        String rpsStr = System.getenv("RECORDS_PER_SECOND");
        if (rpsStr != null) {
            try {
                rps = Integer.parseUnsignedInt(rpsStr);
            } catch (NumberFormatException e) {
                LOG.warn(String.format("Error parsing env var RECORDS_PER_SECOND [%s]: %s. Using %s.",
                        rpsStr,
                        e.getMessage(),
                        rps));
            }
        }
        RECORDS_PER_SECOND = rps;
        LOG.info(String.format("RECORDS_PER_SECOND: %d", RECORDS_PER_SECOND));
    }

    private static boolean streamExists(String kinesisStreamNameStr, Region kinesisRegion) {
        final AmazonKinesis client = AmazonKinesisClient.builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(KINESIS_REGION.getName())
                .build();
        try {
            final DescribeStreamResult describeStreamResult = client.describeStream(kinesisStreamNameStr);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        final AtomicLong completed = new AtomicLong(0);
        final AtomicLong sequenceNumber = new AtomicLong(0);

        final KinesisProducerConfiguration kinesisConfig = new KinesisProducerConfiguration()
                .setRecordMaxBufferedTime(1000)
                .setMaxConnections(4)
                .setRequestTimeout(60000)
                .setRegion(KINESIS_REGION.getName());
        KinesisProducer kinesisProducer = new KinesisProducer(kinesisConfig);

        final int STATS_INTERVAL_SECONDS = 15;
        Thread progress = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long put = sequenceNumber.get();
                    long done = completed.get();
                    // Numerous metrics are available from the KPL locally, as
                    // well as uploaded to CloudWatch. See the metrics
                    // documentation for details.
                    //
                    // KinesisProducer provides methods to retrieve metrics for
                    // the current instance, with a customizable time window.
                    // This allows us to get sliding window statistics in real
                    // time for the current host.
                    //
                    // Here we're going to look at the number of user records and bytes
                    // put over the last STATS_INTERVAL_SECONDS.
                    ShardMetrics metrics = new ShardMetrics(STATS_INTERVAL_SECONDS);

                    try {
                        kinesisProducer.getMetrics("UserRecordsPut", STATS_INTERVAL_SECONDS).forEach(metric -> {
                            metric.getDimensions().forEach((dim, val) -> {
                                    metrics.setShardKinesisRecordsPut(val, metric.getSum());
                            });
                        });
                        kinesisProducer.getMetrics("UserRecordsDataPut", STATS_INTERVAL_SECONDS).forEach(metric -> {
                            metric.getDimensions().forEach((dim, val) -> {
                                    metrics.setShardKinesisRecordsDataPut(val, metric.getSum());
                            });
                        });
                        kinesisProducer.getMetrics("AllErrors", STATS_INTERVAL_SECONDS).forEach(metric -> {
                            metric.getDimensions().forEach((dim, val) -> {
                                    metrics.setShardKinesisErrors(val, metric.getSum());
                            });
                        });

                        metrics.getShardNames().forEach(name -> {
                            LOG.info(metrics.getShardMetic(name).toString());
                        });

                    } catch (Exception e) {
                        LOG.error("Unexpected error getting metrics", e);
                        System.exit(1);
                    }

                    try {
                        Thread.sleep(STATS_INTERVAL_SECONDS * 1000);
                    } catch (InterruptedException e) {}
                }
            }
        });
        progress.start();

        // Result Handler
        final FutureCallback<UserRecordResult> callback = new FutureCallback<UserRecordResult>() {
            @Override
            public void onSuccess(UserRecordResult userRecordResult) {
                completed.incrementAndGet();
            }

            @Override
            public void onFailure(Throwable t) {
                // We don't expect any failures during this sample. If it
                // happens, we will log the first one and exit.
                if (t instanceof UserRecordFailedException) {
                    final Attempt last = Iterables.getLast(
                            ((UserRecordFailedException) t).getResult().getAttempts());
                    LOG.error(String.format(
                            "Record failed to put - %s : %s",
                            last.getErrorCode(), last.getErrorMessage()));
                }
                LOG.error("Exception during put", t);
                System.exit(1);
            }
        };
        // Put a record into KPL at a fixed rate
        final Runnable putRecords = () -> {
            final int outstandingRecordsCount = kinesisProducer.getOutstandingRecordsCount();
            if (outstandingRecordsCount < 2*RECORDS_PER_SECOND) {
                // LOG.info("Putting [" + RECORDS_PER_SECOND + "] records");
                int count = 0;
                while (count < RECORDS_PER_SECOND) {
                    final long number = sequenceNumber.getAndIncrement();
                    final ByteBuffer data = Utils.generateData(number, DATA_SIZE);
                    final String partitionKey = UUID.randomUUID().toString();
                    final ListenableFuture<UserRecordResult> f = kinesisProducer.addUserRecord(KINESIS_STREAM_NAME, partitionKey, data);
                    Futures.addCallback(f, callback);
                    count++;
                }
            } else {
                // LOG.info("OutStandingRecordsCount = [" + outstandingRecordsCount + "]. Skipping write.");
            }
        };
        LOG.info("Starting....");
        EXECUTOR_SERVICE.scheduleAtFixedRate(putRecords, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
