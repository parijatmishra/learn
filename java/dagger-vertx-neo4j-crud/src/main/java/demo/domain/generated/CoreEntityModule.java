package demo.domain.generated;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import lib.vertx.VertxRestResource;

import javax.inject.Singleton;

/**
 * Dagger 2 module.
 */
@Module
public class CoreEntityModule
{
    @Provides
    @Singleton
    @IntoSet
    VertxRestResource get(CoreEntityRestResource _arg)
    {
        return _arg;
    }
}
