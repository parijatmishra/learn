package demo.server;

import dagger.Module;
import dagger.Provides;
import lib.db.IDGenerator;
import lib.db.UUIDGenerator;

import javax.inject.Singleton;

/**
 * Provide a UUID based IDGenerator.
 */
@Module
public class UUIDGeneratorModule {
    @Provides
    @Singleton
    public static IDGenerator getUUIDGenerator() {
        return new UUIDGenerator();
    }
}
