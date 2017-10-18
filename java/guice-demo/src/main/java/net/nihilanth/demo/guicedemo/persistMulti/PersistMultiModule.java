package net.nihilanth.demo.guicedemo.persistMulti;

import com.google.inject.*;
import com.google.inject.persist.jpa.JpaPersistModule;
import net.nihilanth.demo.guicedemo.persistMulti.one.ServiceOne;
import net.nihilanth.demo.guicedemo.persistMulti.two.ServiceTwo;

/**
 * Not a Guice module.  Uses two PrivateModules.
 */
public class PersistMultiModule {

    private static Module getTwo() {
        PrivateModule two = new PrivateModule() {
            @Override
            protected void configure() {
                install(new JpaPersistModule("unitTwo"));
                bind(ServiceTwo.class).in(Singleton.class);
                expose(ServiceTwo.class);
            }
        };
        return two;
    }

    private static Module getOne() {
        PrivateModule one = new PrivateModule() {
            @Override
            protected void configure() {
                install(new JpaPersistModule("unitOne"));
                bind(ServiceOne.class).in(Singleton.class);
                expose(ServiceOne.class);
            }
        };
        return one;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(getOne(), getTwo());
        OrchestrationService service = injector.getInstance(OrchestrationService.class);
        service.process("Alice", "Bob");
    }
}
