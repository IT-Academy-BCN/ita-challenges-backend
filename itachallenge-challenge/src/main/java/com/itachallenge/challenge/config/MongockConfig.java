package com.itachallenge.challenge.config;

import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mongock")
public class MongockConfig {

    @Value("${mongock.migration-scan-package}")
    private String migrationScanPackage;

    @Value("${mongock.transactionEnabled}")
    private boolean transactionEnabled;

    @Bean
    public MongockInitializingBeanRunner getBuilder(MongoClient mongoClient,
                                                    ApplicationContext context) {
        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(mongoClient, "challenges"))
                .addMigrationScanPackage(migrationScanPackage)
                .setSpringContext(context)
                .setTransactionEnabled(transactionEnabled)
                .buildInitializingBeanRunner();
    }
}
