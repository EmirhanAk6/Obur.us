package com.project.Obur.us.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    @Value("${spring.neo4j.pool.max-connection-pool-size:10}")
    private int poolSize;

    @Value("${spring.neo4j.pool.connection-acquisition-timeout:60s}")
    private Duration acquisitionTimeout;

    @Bean
    public Driver neo4jDriver() {
        log.info("Initializing Neo4j driver with URI: {}", uri);

        return GraphDatabase.driver(
                uri,
                AuthTokens.basic(username, password),
                org.neo4j.driver.Config.builder()
                        .withMaxConnectionPoolSize(poolSize)
                        .withConnectionAcquisitionTimeout(
                                acquisitionTimeout.toMillis(),
                                java.util.concurrent.TimeUnit.MILLISECONDS
                        )
                        .withLogging(org.neo4j.driver.Logging.slf4j())
                        .build()
        );
    }
}


