package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/* You can run RabbbitMQ locally with `docker run -d -h my-rabbit --name my-rabbit -p 5672:5672 rabbitmq` */
@EnableBinding(MessageChannels.class)
@RestController
@SpringBootApplication
public class ProducerApplication {

    private final MessageChannels channels;

    @Autowired
    public ProducerApplication(MessageChannels channel) {
        this.channels = channel;
    }

    /* This endpoint sends the greeting to our channel, so it generates an event.
    * Our event bus is RabbitMQ due to our dependencies in pom.xml
    * See application.yml properties.
    * See also `livelessons-integration-consumer`
    * If you call this endpoint and the consumer is running, you'll see your greeting in consumer's console.
    * If consumer is not running, you'll see your greetings as soon as consumer comes up online. */
    @RequestMapping(method = RequestMethod.GET, value = "/greet/{name}")
    void greet(@PathVariable String name) {
        Message<String> msg = MessageBuilder.withPayload(name).build();
        this.channels.output().send(msg);
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}

interface MessageChannels {

    @Output
    MessageChannel output();
}
