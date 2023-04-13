package com.scheduler.agenda.util;

public class Task {

    private int taskId;
    private String task;
    private int taskPriority;
    private String serviceAddress;

    public Task() {
        taskId = 0;
        task = null;
        taskPriority = 0;
        serviceAddress = null;
    }

    public Task(int taskId, String task, int taskPriority, String serviceAddress) {
        this.taskId = taskId;
        this.task = task;
        this.taskPriority = taskPriority;
        this.serviceAddress = serviceAddress;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
