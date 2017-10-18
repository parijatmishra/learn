package demo.server;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lib.vertx.VertxRestResource;
import lib.vertx.VertxServer;
import lib.web.DefaultLinkRegistry;
import lib.web.LinkRegistry;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

/**
 * A Dagger2 Module for all things Vert.x.
 */
@Module
public class VertxModule {
    @Provides @Singleton
    public static Vertx getVertx() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        VertxOptions vertxOptions = new VertxOptions().setWorkerPoolSize(20);
        Vertx vertx = Vertx.vertx(vertxOptions);
        return vertx;
    }

    @Provides @Named("ApiBasePath")
    public static String getApiBasePath() {
        return "/api/v1";
    }

    @Provides @Singleton
    public static VertxServer getHttpServer(Vertx vertx, @Named("ApiBasePath") final String apiBasePath, Set<VertxRestResource> resources) {
        Integer listenPort = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
        VertxServer server = new VertxServer(vertx, listenPort, apiBasePath, resources);
        return server;
    }

    @Provides @Singleton
    public static LinkRegistry getApiURLTracker(@Named("ApiBasePath") final String apiBasePath) {
        return new DefaultLinkRegistry(apiBasePath);
    }
}
