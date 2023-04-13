package com.scheduler.agenda;

import com.scheduler.agenda.Persistence.TaskEntity;
import com.scheduler.agenda.util.Task;
import com.scheduler.agenda.util.TaskMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mongodb.assertions.Assertions.assertNull;
import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperTests {

    private TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void mapperTests() {
        assertNotNull(mapper);

        Task task = new Task(1, "n", 1, "sa");
        TaskEntity entity = mapper.apiToEntity(task);

        assertEquals(task.getTaskId(), entity.getTaskId());
        assertEquals(task.getTaskId(), entity.getTaskId());
        assertEquals(task.getTask(), entity.getTask());
        assertEquals(task.getTaskPriority(), entity.getTaskPriority());

        Task task2 = mapper.entityToApi(entity);

        assertEquals(task2.getTaskId(), task.getTaskId());
        assertEquals(task.getTaskId(), task2.getTaskId());
        assertEquals(task.getTask(), task2.getTask());
        assertEquals(task.getTaskPriority(), task2.getTaskPriority());
        assertNull(task2.getServiceAddress());
    }
}
