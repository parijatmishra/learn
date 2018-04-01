package net.nihilanth.demo.kinesis.consumer.integers.kcl;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 *
 */
public class Main
{
    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static String APP_NAME;
    private static Region KINESIS_REGION;
    private static String KINESIS_STREAM_NAME;

    public static void main(String[] args) throws UnknownHostException
    {
        String appNameStr = System.getenv("APP_NAME");
        if (appNameStr == null) {
            LOG.error("Environment variable APP_NAME is required");
            System.exit(1);
        }
        APP_NAME = appNameStr;

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

        final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
        final String workerId = APP_NAME + ":" + UUID.randomUUID();

        final StatsLogger statsLogger = new StatsLogger(APP_NAME, workerId, 5);

        final KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(APP_NAME, KINESIS_STREAM_NAME, credentialsProvider, workerId)
                .withRegionName(KINESIS_REGION.getName())
                .withCleanupLeasesUponShardCompletion(true)
                .withInitialPositionInStream(InitialPositionInStream.LATEST);
        final Worker worker = new Worker.Builder()
                .recordProcessorFactory(new IntegerRecordProcessorFactory(APP_NAME, workerId, statsLogger))
                .config(config)
                .build();

        // Start logging stats
        Thread statsLoggerThread = new Thread(statsLogger);
        statsLoggerThread.start();

        LOG.info("Starting [" + APP_NAME + "] worker ID: [" + workerId + "]");
        try {
            worker.run();
            statsLoggerThread.interrupt();
        } catch (Throwable t) {
            LOG.error("ERROR", t);
            System.exit(1);
        }
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

}
