package com.scheduler.agenda.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

public class Event<K, O> {

    public enum Type{
        CREATE,
        DELETE
    }

    public K key;
    public O object;

    public Type eventType;
    public ZonedDateTime createdAt;

    public Event(K key, O object, Type eventType, ZonedDateTime createdAt) {
        this.key = key;
        this.object = object;
        this.eventType = eventType;
        this.createdAt = createdAt;
    }

    public K getKey() {
        return key;
    }

    public O getObject() {
        return object;
    }

    public Type getEventType() {
        return eventType;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
