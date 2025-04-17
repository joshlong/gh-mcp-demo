package com.example.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    MethodToolCallbackProvider methodToolCallbackProvider(DogAdoptionScheduler scheduler) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(scheduler)
                .build();
    }
}


@Component
class DogAdoptionScheduler {

    @Tool(description = "schedule an appointment to pickup or adopt a dog from a Pooch Palace location")
    String scheduleAppointment(
            @ToolParam(description = "the id of the dog to pickup") int dogId,
            @ToolParam(description = "the name of the dog to pickup") String dogName
    ) {
        System.out.println("Scheduling appointment for dog %d (%s)".formatted(dogId, dogName));
        return Instant
                .now()
                .plus(3, ChronoUnit.DAYS)
                .toString();
    }
}