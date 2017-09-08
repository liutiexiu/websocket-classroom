package me.tiezhu.queue;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static me.tiezhu.queue.QueueConfig.Q_NAME_CLASSROOM;
import static me.tiezhu.queue.QueueConfig.Q_NAME_USER;

/**
 * Created by liushuai on 2017/9/6.
 */
@Component
public class QueueSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSender.class);

    @Autowired
    private AmqpTemplate queueTemplate;

    public void sendToClassroomQueue(JSONObject msg) {
        LOGGER.debug("send to classroom queue, msg:{}", msg);
        queueTemplate.convertAndSend(Q_NAME_CLASSROOM, msg.toString());
    }

    public void sendToUserQueue(JSONObject msg) {
        LOGGER.debug("send to user queue, msg:{}", msg);
        queueTemplate.convertAndSend(Q_NAME_USER, msg.toString());
    }
}
