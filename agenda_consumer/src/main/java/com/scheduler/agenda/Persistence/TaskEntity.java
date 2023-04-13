package com.scheduler.agenda.Persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Tasks")
public class TaskEntity {

    @Id
    private Long id;
    @Version
    private Integer version;
    private String task;
    private int taskPriority;

    @Indexed(unique = true)
    private int taskId;

    public TaskEntity() {
    }

    public TaskEntity(int taskId, String task, int taskPriority) {
        this.taskId = taskId;
        this.task = task;
        this.taskPriority = taskPriority;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }
}
