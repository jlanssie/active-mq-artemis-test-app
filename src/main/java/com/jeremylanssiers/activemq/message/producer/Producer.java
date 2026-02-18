package com.jeremylanssiers.activemq.message.producer;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.Nullable;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Producer implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Producer.class.getName());

    @Autowired
    @Qualifier("jmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Value("${app.jms.destination-name:queue}")
    private String destinationName;

    @Value("${app.jms.routing-type:ANYCAST}")
    private String routingType;

    public static void main(String[] args) {
        SpringApplication.run(Producer.class, args);
    }

    @Nullable
    private static Destination getDestination(Session session, String name, boolean multicast) {
        try {
            return multicast ? session.createTopic(name) : session.createQueue(name);
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Could not create destination: {0}", name);
            return null;
        }
    }

    @Nullable
    private static TextMessage getTextMessage(Session session, String message) {
        try {
            TextMessage textMessage = session.createTextMessage(message);

            textMessage.setStringProperty("MESSAGE_CORRELATION_ID", UUID.randomUUID().toString());
            textMessage.setStringProperty("_AMQ_GROUP_ID", UUID.randomUUID().toString());
            textMessage.setStringProperty("MESSAGE_PRODUCER", "messageProducer");
            textMessage.setStringProperty("MESSAGE_REPLY_USER", "messageReplyUser");
            textMessage.setStringProperty("MESSAGE_ID", UUID.randomUUID().toString());

            return textMessage;
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Could not create message.", e);
            return null;
        }
    }

    public static void sendMessage(Session session, @Nullable Destination destination, @Nullable TextMessage textMessage) {
        if (destination != null && textMessage != null) {
            try (MessageProducer producer = session.createProducer(destination)) {
                producer.send(textMessage);
                logger.log(Level.INFO, "Sent message: {0}", textMessage.getText());
            } catch (JMSException e) {
                logger.log(Level.SEVERE, "Could not send message.", e);
            }
        }
    }

    @Override
    public void run(String... args) {
        // Automatically uses settings from application.yaml
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            connection.start();

            boolean isMulticast = "MULTICAST".equalsIgnoreCase(routingType);
            Destination destination = getDestination(session, destinationName, isMulticast);
            TextMessage textMessage = getTextMessage(session, "Hello from Spring Boot!");

            sendMessage(session, destination, textMessage);

            logger.info("Message process complete.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "JMS Error during run execution", e);
        }
    }
}
