package com.jeremylanssiers.activemq.message.producer;

import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

@Slf4j
@SpringBootApplication
public class Producer implements CommandLineRunner {
    private final JmsTemplate jmsTemplate;

    @Value("${app.jms.routing-context.address:messageAddress}")
    private String destinationName;

    @Value("${app.jms.routing-context.routing-type:ANYCAST}")
    private String routingType;

    @Value("${app.jms.message-producer:messageProducer}")
    private String producerName;

    @Value("${app.jms.message-reply-user:messageReplyUser}")
    private String replyUser;

    public Producer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(Producer.class, args);
    }

    @Override
    public void run(String... args) {
        boolean isMulticast = "MULTICAST".equalsIgnoreCase(routingType);
        jmsTemplate.setPubSubDomain(isMulticast);

        jmsTemplate.send(destinationName, session -> {
            TextMessage message = session.createTextMessage("Hello from a cleaner Spring Boot!");

            message.setStringProperty("MESSAGE_CORRELATION_ID", UUID.randomUUID().toString());
            message.setStringProperty("_AMQ_GROUP_ID", UUID.randomUUID().toString());
            message.setStringProperty("MESSAGE_PRODUCER", producerName);
            message.setStringProperty("MESSAGE_REPLY_USER", replyUser);
            message.setStringProperty("MESSAGE_ID", UUID.randomUUID().toString());

            return message;
        });

        log.info("Sent message to {} as {}", destinationName, routingType);
    }
}
