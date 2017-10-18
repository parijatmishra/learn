Google Guice Cheatsheet
=======================

Linked Bindings
---------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/linked*

A binding *links* a type to its implementation.  Clients express
dependencies with injection points annotated with `@Inject`.  Typically,
the dependency is on an interface, and a *concrete* implementation of
the interface must be instantiated and injected into the client.

Assuming the following interface, and a client that depends on it:

    interface TransactionLog {... }

    class BillingService {
        @Inject
        BillingService(TransactionLog log) {...}
    }

Suppose we have an implementation that we would like to construct
and inject into the client:

    class DatabaseTransactionLog implements TransactionLog {...}

A Guice *module* needs to be written, and the link between the interface
and its implementation described:

    class BillingModule extends AbstractModule {
        public void configure() {
            bind(TransactionLog.class).to(DatabaseTransactionLog.class)
        }
        ...
    }
    
Now, we can get a instance of `BillingService` with an implementation of
`TransactionLog` already injected in it:


    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

Selecting Between Multiple Implementations: @Named Bindings
-----------------------------------------------------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/named*

When more than one implementation of a type exists and the client
is aware of them, the client can choose one of the implementations,
using annotations.  For simple cases, Guice's built-in `@Named(...)`
and `Names.named(...)` mechanism can be used.

E.g., a type and two implementations:

    interface TransactionLog {...}
    class DatabaseTransactionLog implements TransactionLog {...}
    class RemoteTransactionLog implements TransactionLog {...}


Define the bindings, disambiguate using `Names.named(...)`:

    class NamedBillingModule extends AbstractModule {
        public void configure() {
            bind(TransactionLog.class)
              .annotatedWith(Names.named("local"))
              .to(DatabaseTransactionLog.class);
              
            bind(TransactionLog.class)
              .annotatedWith(Names.named("remote"))
              .to(RemoteTransactionLog.class);


The client can choose which implementation to use at the injection point
using `@Named(...)`:

        @Inject
        public BillingService(@Named("remote") TransactionLog transactionLog)
        ...

Selecting Between Multiple Implementations: Custom Annotations
--------------------------------------------------------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/annotations*

The `@Named` mechanism is not type safe.  If there is a mismatch
between the argument to `@Named(...)` and `Names.named(...)` because
of a typo, there will be no compile time error.  Only at runtime, when
Guice attempts to find a conforming implementation, will there be a
runtime error.  Such errors can be avoided by using custom annotations.

For example, we can have annotations corresponding to the instances of
`@Named` above:

    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public @interface Local {}

    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public @interface Remote {}

Define the bindings:


        bind(TransactionLog.class)
            .annotatedWith(Local.class)
            .to(DatabaseTransactionLog.class);
        
        bind(TransactionLog.class)
            .annotatedWith(Remote.class)
            .to(RemoteTransactionLog.class);
                
At the injection point:

            @Inject
            public BillingService(@Local TransactionLog transactionLog) {

Here, if there is a typo anywhere, the compiler will flag it as an error.

Instance Bindings
-----------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/instance*

It is possible to bind a type to a single, particular instance, if for
some reason we need to create that instance ourselves.  For very
easy to create instances, we can use `toInstance(...)`:

Define the binding:

        bind(String.class)
          .annotatedWith(Names.named("JDBC_URL")
          .toInstance("jdbc:mysql://localhost/somedb")

Create dependency in the client:
        
    public DatabaseTransactionLog implements TransactionLog {
        ...
        @Inject
        DatabaseTransactionLog(@Named("JDBC_URL") String jdbcUrl) {...}
    }

Constructing Implementation Objects: @Provides methods
------------------------------------------------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/providesMethods*

If custom code has to be written to create an object of a class, use
a method annotated with `@Provides`.  The method must be defined
in a Guice module.  The return type of the method is the bound type, i.e.
the type given to `bind(...)`.

E.g.:

    class ProvidesMethodBillingModule extends AbstractModule {
        
         @Override protected void configure() {}
         
         @Provides
         TransactionLog provideDbTransactionLog() {
             DatabaseTransactionLog txLog =
               new DatabaseTransactionLog("jdbc:mysql://localhost/db");
             ... 
             return txLog;
         }
    }
    
Annotations can also be used.  The equivalent of `.annotatedWith` is:

         @Provides @Named("local")
         TransactionLog provideDbTransactionLog() {
             DatabaseTransactionLog txLog =
               new DatabaseTransactionLog("jdbc:mysql://localhost/db");
             ... 
             return txLog;
         }

There is no change in how the client specifies its dependency.

Constructing Implementation Objects: Provider<T> and Provider bindings
----------------------------------------------------------------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/providerBindings*

If the code to create an object is too complex to put in a `@Provides`
method, it can be moved into a class that implements the `Provider<T>`
interface.

        bind(TransactionLog.class)
            .toProvider(DatabaseTransactionLogProvider.class);

The Provider class itself can receive `@Inject` dependencies.

    class DatabaseTransactionLogProvider
        implements Provider<TransactionLog> {
        
        @Inject
        DatabaseTransactionLogProvider(@Named("JDBC_URL") String url) {
            this.jdbcUrl = url;
        }
        
        @Override
        TransactionLog get() {
            return new DatabaseTransactionLog(this.jdbcUrl);
        }

There is no change in how the client specifies it's dependencies.

Providers
---------

For every injectable type `T`, the client can also request for an
injection of `Provider<T>`, and call `T get()` on it.   This is useful 
for getting multiple instances of `T`.

This works for any type `T`, not just for which a `Provider<T>` has
been written.  Guice automatically creates a `Provider<T>` for every
injectable type.

Just-in-time Bindings or Automatic Bindings
-------------------------------------------

**Sample:** *src/main/java/net/nihilanth/demo/guicedemo/justintime*

In some cases, Guice can create bindings automatically without the need
for a `bind(...).toXXX`.  These are called just-in-time (JIT) or
implicit bindings.

Interfaces can create a JIT binding to an implementing class by using
`@ImplementedBy`:


    @ImplementedBy(DatabaseTransactionLog.class)
    interface TransactionLog {...}
    
    class DatabaseTransactionLog implements TransactionLog {}

Implementing classes should have either a public no-args constructor,
or have an constructor annotated by `@Inject`.

If the implementing class is an inner class, it must be a *static* inner
class.

Interfaces can create a JIT binding to a provider class by using
`@ProvidedBy`:

    @ProvidedBy(PaypalCreditCardProcessorProvider.class)
    interface CreditCardProcessor {...}

In these cases, an explicit `bind` is not needed, but can be used
to override the default JIT binding.

Guice-persist extension, JPA and multiple persistence units
-----------------------------------------------------------

**Sample**: *src/main/java/net/nihilanth/demo/guicedemo/persistMulti*

Consider the scenario where we have two different databases being
used in the same application, and the application is using JPA.

Two different persistence units must exist, each with their own *Entity*
classes, own transactions and own `EntityManager`s.  The two different
`EntityManager`s need to be injected in the right client.

The `guice-persist` extension provides a `JpaPersistModule` to create
an `EntityManagerFactory` or `EntityManager` from a persistence unit
read from the standard `persistence.xml` file.  Its bindings can be
imported in our own module using `install(...)`.  Then we would be able
to let Guice inject the required JPA types in our client classes.

However, the challenge is that if there are >1 persistence units, then
Guice will not know which persistence unit to inject into our client. We
are not able to annotate the created `EntityManager`s in any way.

The solution is to use a `PrivateModule` for each persistence unit.
In its `configure(...)` method, we install the `JpaPersistModule` and
all client classes that need that persistence unit.

If any of the client classes in the private module need to be visible
elsewhere to be injected, then we selectively `expose(...)` those
classes.  The JPA entities are not exposed, as they would interfere
with JPA entities from other modules.

**NOTE:** The one thing to watch out for with `guice-persist` is that you
must obtain an instance of `PersistService` and call `start()` on it
**before** injecting `EntityManager`, or the injection will fail. So
in our client classes, we do:

    public class ServiceOne {
    
        @Inject
        public ServiceOne(PersistService persistService) {
            persistService.start();
        }
    
        @Inject
        public void setEm(EntityManager em) {
            System.out.println("ServiceOne.setEm");
            this.em = em;
        }

        ...
    }

The `JpaPersistModule` requires the presence of a `persistence.xml` file.