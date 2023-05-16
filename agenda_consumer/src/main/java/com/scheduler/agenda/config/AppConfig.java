package com.scheduler.agenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoClientFactoryBean;

@Configuration
public class AppConfig {

    @Bean
    public ReactiveMongoClientFactoryBean mongoClient() {
        ReactiveMongoClientFactoryBean mongoC = new ReactiveMongoClientFactoryBean();
        mongoC.setHost("localhost");
        return mongoC;
    }
}
