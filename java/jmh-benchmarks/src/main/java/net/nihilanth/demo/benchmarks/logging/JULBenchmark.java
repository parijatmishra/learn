package net.nihilanth.demo.benchmarks.logging;

import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.*;

/**
 * Benchmark j.u.l logging framework, with:
 * <p>
 * - plain string logging
 * - logging with string interpolation / arguments
 * - logging with string interpolation / arguments when log level is disabled
 * - exception logging
 */
public class JULBenchmark {
    private static final int N = 1_000;
    private static final Logger logger;

    static {
        // System.setProperty("java.util.logging.SimpleFormatter.format",
        //        "[%1$tc] [%4$s] [%3$s] %5$s%6$s%n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String ts = dateFormat.format(new Date());
        String filename = "JULBenchmark_" + ts + ".log";
        Handler fh;
        try {
            fh = new FileHandler(filename);
        } catch (IOException e) {
            e.printStackTrace();
            fh = new ConsoleHandler();
        }


        // Formatter formatter = new SimpleFormatter();
        // fh.setFormatter(formatter);
        logger = Logger.getLogger(JULBenchmark.class.getCanonicalName());
        logger.addHandler(fh);
        logger.setUseParentHandlers(false);
    }

    @Benchmark
    public void logString() {
        logger.info("Log message");

    }

    @Benchmark
    public void logStringWithArg() {
        logger.info(String.format("Log message at %s", new Date()));
    }

    @Benchmark
    public void logStringWithArgBelowLevelThreshold() {
        logger.fine(String.format("Log message at %s", new Date()));
    }

    @Benchmark
    public void logStringWithArgWithLevelCheck() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("Log message at %s", new Date()));
        }
    }

    @Benchmark
    public void logException() {
        try {
            throw new Exception("Simulated error");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR", e);
        }
    }
}
