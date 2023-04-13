package com.example.agendaproducer.integration;

import com.example.agendaproducer.Exception.HttpErrorInfo;
import com.example.agendaproducer.Exception.InvalidInputException;
import com.example.agendaproducer.Exception.NotFoundException;
import com.example.agendaproducer.Persistence.Task;
import com.example.agendaproducer.Util.Event;
import com.example.agendaproducer.Util.ServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import static com.example.agendaproducer.Util.Event.Type.CREATE;
import static com.example.agendaproducer.Util.Event.Type.DELETE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
public class AgendaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(AgendaProducer.class);

    private final WebClient webClient;
    private final Scheduler eventScheduler;
    private final ObjectMapper objectMapper;
    private final StreamBridge streamBridge;
    private final ServiceUtil serviceUtil;
    private final String AgendaServiceURL = "https://agenda";

    @Autowired
    public AgendaProducer(WebClient.Builder webClient, @Qualifier("publishEventScheduler") Scheduler eventScheduler,
                          ObjectMapper objectMapper, StreamBridge streamBridge, ServiceUtil serviceUtil) {
        this.webClient = WebClient.builder().build();
        this.eventScheduler = eventScheduler;
        this.objectMapper = objectMapper;
        this.streamBridge = streamBridge;
        this.serviceUtil = serviceUtil;
    }

    public Mono<Task> createTask(Task body) {
        return Mono.fromCallable(() -> {
            sendMessage("tasks-out-0", new Event(CREATE, body.getTaskId(), body));
            return body;
        }).subscribeOn(eventScheduler);
    }

    @Retry(name = "task")
    @TimeLimiter(name = "task")
    @CircuitBreaker(name = "task", fallbackMethod = "getTaskFallbackValue")
    public Mono<Task> getTask(HttpHeaders headers, int taskId, int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(AgendaServiceURL +
                "/task/{taskId}?delay={delay}&faultPercent={faultPercent}").build(taskId, delay, faultPercent);
        LOG.debug("Will call getTask API in URL: {}", url);

        return webClient.get().uri(url).headers(h -> h.addAll(headers)).retrieve().bodyToMono(Task.class).log(LOG.getName(), Level.FINE)
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException wcre)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        HttpStatusCode statusCode = wcre.getStatusCode();
        if (statusCode.equals(NOT_FOUND)) {
            return new NotFoundException(getErrorMessage(wcre));
        } else if (statusCode.equals(UNPROCESSABLE_ENTITY)) {
            return new InvalidInputException(getErrorMessage(wcre));
        }
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
        return ex;
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private Mono<Task> getTaskFallbackValue(HttpHeaders headers, int taskId, int delay, int faultPercent,
                                            CallNotPermittedException ex) {
        LOG.warn("Creating a fail-fast fallback product for taskId = {}", taskId);
        if (taskId < 1) {
            throw new InvalidInputException("Invalid taskId: " + taskId);
        }

        if (taskId == 13) {
            String errMsg = "task Id: " + taskId + "not found in fallback cache";
            LOG.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return Mono.just(new Task(taskId, "Fallback product" + taskId, taskId, serviceUtil.getServiceAddress()));
    }

    public Mono<Void> deleteProduct(int productId) {
        Mono.fromCallable(() -> {
            sendMessage("tasks-out-0", new Event(DELETE, productId, null));
            return null;
        }).subscribeOn(eventScheduler);
        return null;
    }

    private void sendMessage(String str, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), str);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(str, message);
    }
}
