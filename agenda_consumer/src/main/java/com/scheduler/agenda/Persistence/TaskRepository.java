package com.scheduler.agenda.Persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, Long> {
    Mono<TaskEntity> findTaskById(int taskId);
}
