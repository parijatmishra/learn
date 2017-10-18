package net.nihilanth.demo.benchmarks.logging;

import org.openjdk.jmh.annotations.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Benchmark SLF4J logging framework, with:
 *
 * - plain string logging
 * - logging with string interpolation / arguments
 * - logging with string interpolation / arguments when log level is disabled
 * - exception logging
 */
public class SLF4JBenchmark {
    private static final Logger logger = LoggerFactory.getLogger(SLF4JBenchmark.class);

    @Benchmark
    public void logString() {
        logger.info("Log message");

    }

    @Benchmark
    public void logStringWithArg() {
        logger.info("Log message at {}", new Date());
    }

    @Benchmark
    public void logStringWithArgBelowLevelThreshold() {
        logger.debug("Log message at {}", new Date());
    }

    @Benchmark
    public void logStringWithArgWithLevelCheck() {
        if (logger.isDebugEnabled()) {
            logger.debug("Log message at {}", new Date());
        }
    }

    @Benchmark
    public void logException() {
        try {
            throw new Exception("Simulated error");
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
    }
}
