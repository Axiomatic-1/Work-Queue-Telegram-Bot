package com.telegrambot.workqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkqueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkqueueApplication.class, args);
        System.out.println("hello");
    }


}
