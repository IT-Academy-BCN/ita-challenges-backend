package com.itachallenge.challenge.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class SpringMongoDBConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoConnectionString;

    @Value("${mongock.migration-scan-package}")
    private String migrationScanPackage;

    @Value("${mongock.transactionEnabled}")
    private boolean transactionEnabled;

    //to avoid _class attribute in mongoDB
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory,
                                                       MongoMappingContext context,
                                                       BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }

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

    @Bean
    MongoClient mongoClient() {

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build());
    }

}