package com.scheduler.agenda.util;

import com.scheduler.agenda.Persistence.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mappings({
            //@Mapping(target = "taskId", ignore = true)
    })
    Task entityToApi(TaskEntity entity);

    @Mappings({
    @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)
    })
    TaskEntity apiToEntity(Task api);
}
