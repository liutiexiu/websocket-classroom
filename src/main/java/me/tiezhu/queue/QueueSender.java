package me.tiezhu.queue;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static me.tiezhu.queue.QueueConfig.Q_NAME_HELLO;

/**
 * Created by liushuai on 2017/9/6.
 */
@Component
public class QueueSender {

    @Autowired
    private AmqpTemplate queueTemplate;

    public void sentToHelloQueue(String msg) {
        System.out.println("queue sender: sending to hello queue, " + msg);
        queueTemplate.convertAndSend(Q_NAME_HELLO, msg);
    }
}
