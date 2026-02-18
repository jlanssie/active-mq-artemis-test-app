package com.jeremylanssiers.activemq.message.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Subscriber implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Application started.");
    }
}
