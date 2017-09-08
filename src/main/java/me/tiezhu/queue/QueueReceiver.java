package me.tiezhu.queue;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static me.tiezhu.queue.QueueConfig.Q_NAME_HELLO;

/**
 * Created by liushuai on 2017/9/6.
 */
@Component
public class QueueReceiver {
    @RabbitHandler
    @RabbitListener(queues = Q_NAME_HELLO)
    public void process(String hello) {
        System.out.println("queue Receive:" + hello);
    }
}
