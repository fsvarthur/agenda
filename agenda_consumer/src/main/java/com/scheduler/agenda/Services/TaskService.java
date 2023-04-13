package com.scheduler.agenda.Services;

import com.scheduler.agenda.util.Task;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;

public interface TaskService {

    Mono<Task> createTask(Task task);

    @GetMapping(
            value = "/task/",
            produces = "application/json")
    Mono<Task> getTasks(HttpHeaders headers, int taskId, int delay, int faultPercent);

    Mono<Void> deleteTask(int taskId);
}
