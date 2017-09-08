package me.tiezhu.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liushuai on 2017/9/6.
 */
@Configuration
public class QueueConfig {

    public static final String Q_NAME_HELLO = "hello";

    @Bean
    public Queue helloQueue() {
        return new Queue(Q_NAME_HELLO);
    }
}
