package com.example.agendaproducer;

import com.example.agendaproducer.Persistence.Task;
import com.example.agendaproducer.Util.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.example.agendaproducer.Util.Event.Type.CREATE;
import static com.example.agendaproducer.Util.Event.Type.DELETE;

public class IsSameEventTests {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventObjectCompare() throws JsonProcessingException {
        Event<Integer, Task> event1 = new Event<>(CREATE, 1, new Task(1, "name", 1, null));
        Event<Integer, Task> event2 = new Event<>(CREATE, 1, new Task(1, "name", 1, null));
        Event<Integer, Task> event3 = new Event<>(DELETE, 1, null);
        Event<Integer, Task> event4 = new Event<>(CREATE, 1, new Task(2, "name", 1, null));

        String event1Json = mapper.writeValueAsString(event1);

        assertThat(event1Json, is(sameEventExceptCreatedAt(event2)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event3)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event4)));
    }
}
