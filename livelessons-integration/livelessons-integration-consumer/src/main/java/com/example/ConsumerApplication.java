package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.SubscribableChannel;

interface MessageChannels {

    @Input
    SubscribableChannel input();
}

/* You can run RabbbitMQ locally with `docker run -d -h my-rabbit --name my-rabbit -p 5672:5672 rabbitmq` */
@EnableBinding(MessageChannels.class)
@SpringBootApplication
public class ConsumerApplication {

    /* This listener receives a greeting that is sent by `livelessons-integration-producer`
     * and prints it out in the console.
     *
     * See application.yml properties. */
    @Bean
    IntegrationFlow greetingsFlow(MessageChannels channels) {
        return IntegrationFlows.from(channels.input())
                .handle(String.class, (payload, headers) -> {
                    System.out.println(payload);
                    return null;
                })
                .get();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
