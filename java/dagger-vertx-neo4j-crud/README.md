# java-dagger-vertx-neo4j-crud

A sample application that:
 - exposes a REST API for various entities
 - uses Vertx-web for the web layer
 - uses Neo4J as the DB backend
 - uses Dagger 2 for dependency injection

## Test

To run tests, or to build with tests, you need a Neo4J server running somewhere. The
tests will use the value of environment variable `NEO4J_URL`
to locate the server.  If the variable is not specified,
the server is assumed to be at `bolt://localhost:7687`.  The
integration tests will start an HTTP server, which will by default
listen on port 8000.  If that is not desirable or if some other
program is already using that port, you can change the port
by setting the value of the environment variable `PORT`.

    export PORT=5000
    export NEO4J_URL='bolt://192.168.1.1:7687'
    mvn test

# Build/Package

To build a package skipping tests:

    mvn package -DskipTests=true
    
To run tests and build a package, simply:

    mvn package

The `package` maven goal produces an uberjar at
`target/java-dagger-vertx-neo4j-crud.jar`.

## Run

Run the program like this:

    export NEO4J_URL=...
    java -jar target/java-dagger-vertx-neo4j-crud.jar

## Use

The API is available at `/api` by default.
    