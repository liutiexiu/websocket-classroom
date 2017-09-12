package me.tiezhu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class WebsocketClassroomMain {

    public static void main(String[] args) {
        System.out.println("app starting...");
        SpringApplication.run(WebsocketClassroomMain.class, args);
    }
}
