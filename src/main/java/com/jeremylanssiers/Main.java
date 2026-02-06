package com.jeremylanssiers;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.UUID;

public class Main {
    private static final String BROKER_URL = "tcp://localhost:61616";
    public static final int INDEX_QUEUE_NAME = 0;
    public static final int INDEX_MESSAGE = 1;

    public static void main(String[] args) {
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
             Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            sendMessage(session, args);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void sendMessage(Session session, String[] args) {
        String queueName = args[INDEX_QUEUE_NAME];

        try {
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage(args[INDEX_MESSAGE]);

            for(int index = 2; index < args.length; index=index+2) {
                String headerName = args[index];
                String headerValue = args[index+1].equalsIgnoreCase("randomUuid")?UUID.randomUUID().toString():args[index+1];
                message.setStringProperty(headerName, headerValue);
            }

            producer.send(message);

            System.out.println("Message sent : " + message.getText());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
