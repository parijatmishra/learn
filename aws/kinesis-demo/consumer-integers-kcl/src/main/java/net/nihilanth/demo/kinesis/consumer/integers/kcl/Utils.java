package net.nihilanth.demo.kinesis.consumer.integers.kcl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *
 */
public class Utils
{
    public static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static long extractNumber(ByteBuffer data)
    {
        long ret = 0;
        try {
            final String s = new String(data.array(), Charset.forName("UTF-8"));
            final String[] strings = s.split(" ");
            ret = Long.parseUnsignedLong(strings[0]);
        } catch (IllegalArgumentException e) {
            LOG.error("Could not parse data as string: %s", e.getMessage());
            System.exit(1);
        }

        return ret;
    }
}
