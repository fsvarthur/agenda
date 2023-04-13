package com.scheduler.agenda.config;

import com.scheduler.agenda.Services.TaskService;
import com.scheduler.agenda.util.Event;
import com.scheduler.agenda.util.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final TaskService taskService;

    @Autowired
    public MessageProcessorConfig(TaskService taskService) {
        this.taskService = taskService;
    }

    @Bean
    public Consumer<Event<Integer, Task>> eventConsumer(){
        return event -> {
            LOG.info("Processing type of event");
            switch (event.eventType){
                case CREATE ->{
                    Task task = event.getObject();
                    LOG.info("Create Task with id {}", event.getKey());
                    taskService.createTask(task).block();
                }
                case DELETE -> {
                    int taskId = event.getKey();
                    LOG.info("Deleting Task with id {}", taskId);
                    taskService.deleteTask(taskId).block();
                }
            }
        };
    }
}
