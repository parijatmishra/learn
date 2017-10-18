package demo.server;

import dagger.Module;
import dagger.Provides;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import javax.inject.Singleton;

/**
 * Dagger module for all things Neo4J
 */
@Module
class Neo4JDriverModule {
    @Provides
    @Singleton
    public Driver getNeo4JDriver() {
        String driverUrl = System.getenv().getOrDefault("NEO4J_URL", "bolt://localhost:7687");
        String neo4jUser = System.getenv().getOrDefault("NEO4J_USER", "neo4j");
        String neo4jPassword = System.getenv().getOrDefault("NEO4J_PASSWORD", "neo4j");
        return GraphDatabase.driver(driverUrl, AuthTokens.basic(neo4jUser, neo4jPassword));
    }
}
