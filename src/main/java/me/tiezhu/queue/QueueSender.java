package me.tiezhu.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tiezhu.model.QueueMsgInClassroom;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static me.tiezhu.queue.QueueConfig.Q_NAME_CLASSROOM;
import static me.tiezhu.queue.QueueConfig.Q_NAME_USER;
import static org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_JSON;
import static org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_TEXT_PLAIN;

/**
 * Created by liushuai on 2017/9/6.
 */
@Component
public class QueueSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSender.class);

    @Autowired
    private AmqpTemplate queueTemplate;

    public void sendToClassroomQueue(QueueMsgInClassroom msg) throws JsonProcessingException {
        LOGGER.debug("send to classroom queue, msg:{}", msg);

        MessageProperties properties = new MessageProperties();
        properties.setContentType(CONTENT_TYPE_JSON);
        queueTemplate.convertAndSend(QueueConfig.Q_NAME_CLASSROOM, msg);
    }

    public void sendToUserQueue(JSONObject msg) {
        LOGGER.debug("send to user queue, msg:{}", msg);

        MessageProperties properties = new MessageProperties();
        properties.setContentType(CONTENT_TYPE_TEXT_PLAIN);
//        queueTemplate.convertAndSend(QueueConfig.Q_NAME_USER, msg.toString());
        queueTemplate.convertAndSend(QueueConfig.Q_NAME_USER, new Message(msg.toString().getBytes(), properties));
    }
}
