package demo.domain.generated;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import lib.vertx.VertxRestResource;

import javax.inject.Singleton;

/**
 *
 */
@Module
public class MSOSEntityModule
{
    @Provides
    @Singleton
    @IntoSet
    VertxRestResource get(MSOSEntityRestResource _arg)
    {
        return _arg;
    }
}
