package com.jeremylanssiers.activemq.message.subscriber;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.lang.Nullable;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Subscriber {
    private static final Logger logger = Logger.getLogger(Subscriber.class.getName());

    public static void main(String[] args) {
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            Destination destination = getDestination(session, "queue");
            TextMessage textMessage = getTextMessage(session, "message");

            sendMessage(session, destination, textMessage);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error.", e);
        }
    }

    @Nullable
    private static Destination getDestination(Session session, String queue) {
        try {
            return session.createQueue(queue);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not create destination.", e);
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not create message.", e);
            return null;
        }
    }

    public static void sendMessage(Session session, @Nullable Destination destination, @Nullable TextMessage textMessage) {
        if (destination != null && textMessage != null) {
            try (MessageProducer producer = session.createProducer(destination)) {
                producer.send(textMessage);
                logger.log(Level.INFO, "Sent message: {0}", textMessage.getText());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Could not send message.", e);
            }
        }
    }
}
