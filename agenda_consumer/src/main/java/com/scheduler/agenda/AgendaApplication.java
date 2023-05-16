package com.scheduler.agenda;

import com.scheduler.agenda.Persistence.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@SpringBootApplication
public class AgendaApplication {

    private static final Logger LOG = LoggerFactory.getLogger(AgendaApplication.class);

    @Autowired
    ReactiveMongoOperations mongoTemplate;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
                SpringApplication.run(AgendaApplication.class, args);

        String mongodbDocument = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        String mongodbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        LOG.info("Connected to mongoDb: " + mongodbDocument + " on port" + mongodbPort);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext =
                mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(TaskEntity.class);
        resolver.resolveIndexFor(TaskEntity.class).forEach(indexOps::ensureIndex);
    }

}
