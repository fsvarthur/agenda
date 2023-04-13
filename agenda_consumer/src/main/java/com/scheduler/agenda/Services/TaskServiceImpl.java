package com.scheduler.agenda.Services;

import com.mongodb.DuplicateKeyException;
import com.scheduler.agenda.Exception.InvalidInputException;
import com.scheduler.agenda.Exception.NotFoundException;
import com.scheduler.agenda.Persistence.TaskEntity;
import com.scheduler.agenda.Persistence.TaskRepository;
import com.scheduler.agenda.util.ServiceUtil;
import com.scheduler.agenda.util.Task;
import com.scheduler.agenda.util.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;
import java.time.Duration;
import java.util.Random;
import java.util.logging.Level;

@RestController
public class TaskServiceImpl implements TaskService {

    private final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ServiceUtil serviceUtil;
    private final TaskMapper mapper;
    private final Random randomNumberGenerator = new Random();

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper mapper, ServiceUtil serviceUtil) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Task> createTask(Task task) {
        if (task.getTaskId() < 1) {
            throw new InvalidInputException("Invalid TaskId: " + task.getTaskId());
        }

        TaskEntity taskEntity = mapper.apiToEntity(task);
        Mono<Task> newTaskEntity = taskRepository.save(taskEntity)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException()
                ).map(mapper::entityToApi);
        return newTaskEntity;
    }

    @Override
    public Mono<Task> getTasks(HttpHeaders headers, int taskId, int delay, int faultPercent) {
        if (taskId < 1) {
            throw new InvalidInputException("Invalid input" + taskId);
        }
        LOG.info("Will get product info for id={}", taskId);

        return taskRepository.findTaskById(taskId)
                .map(e -> throwErrorIfBadLuck(e, faultPercent))
                .delayElement(Duration.ofSeconds(delay))
                .switchIfEmpty(Mono.error(new NotFoundException("No task found for id" + taskId)))
                .log(LOG.getName(), Level.FINE)
                .map(mapper::entityToApi)
                .map(e -> setServiceAddress(e));
    }


    @Override
    public Mono<Void> deleteTask(int taskId) {
        if (taskId < 1) {
            throw new InvalidInputException("infornu");
        }
        return taskRepository.findTaskById(taskId).log(LOG.getName(), Level.FINE)
                .map(taskRepository::delete).flatMap(e -> e);
    }

    private TaskEntity throwErrorIfBadLuck(TaskEntity e, int faultPercent) {
        if (faultPercent == 0) {
            return e;
        }

        int randomThreshold = getRandomNumber(1, 100);

        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error ocurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error ocurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong");
        }

        return e;
    }

    private Task setServiceAddress(Task t) {
        t.setServiceAddress(serviceUtil.getServiceAddress());
        return t;
    }

    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
