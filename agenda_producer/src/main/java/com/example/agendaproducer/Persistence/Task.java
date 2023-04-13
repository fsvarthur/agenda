package com.example.agendaproducer.Persistence;

public class Task {

    private int taskId;
    private String tarefa;
    private int taskPriority;
    private String serviceAddress;

    public Task(int taskId, String tarefa, int taskPriority, String serviceAddress) {
        this.taskId = taskId;
        this.tarefa = tarefa;
        this.taskPriority = taskPriority;
        this.serviceAddress = serviceAddress;
    }

    public Task(){
        taskId = 0;
        tarefa = null;
        taskPriority = 0;
        serviceAddress = null;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTarefa() {
        return tarefa;
    }

    public void setTarefa(String tarefa) {
        this.tarefa = tarefa;
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
