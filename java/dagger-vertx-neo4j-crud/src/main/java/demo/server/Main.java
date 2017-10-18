package demo.server;

import dagger.Component;
import demo.domain.generated.*;
import lib.vertx.VertxServer;

import javax.inject.Singleton;

/**
 * Start the server.
 */
public class Main {
    public static void main(String[] args) {
        VertxServer vertxServer = DaggerMain_Server.create().getVertxServer();
        vertxServer.start();
    }

    @Singleton
    @Component(modules = {
            VertxModule.class
            , Neo4JDriverModule.class
            , UUIDGeneratorModule.class
            , CoreEntityModule.class
            , MSEntityModule.class
            , OSEntityModule.class
            , CollEntityModule.class
            , MSOSEntityModule.class
    })
    interface Server {
        VertxServer getVertxServer();
    }
}
